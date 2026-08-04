package com.tplite.banking.transferservice.service.impl;

import com.tplite.banking.common.dto.ApiResponse;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.transferservice.client.AccountClient;
import com.tplite.banking.transferservice.entity.IdempotencyKey;
import com.tplite.banking.transferservice.entity.Transfer;
import com.tplite.banking.transferservice.repository.IdempotencyKeyRepository;
import com.tplite.banking.transferservice.repository.TransferRepository;
import com.tplite.banking.transferservice.service.TransferService;
import com.tplite.banking.transferservice.entity.OutboxEvent;
import com.tplite.banking.transferservice.repository.OutboxEventRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final AccountClient accountClient; // Vũ khí gọi HTTP sang Account Service

    @Transactional
    public String createTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount) {
        
        // 1. KIỂM TRA IDEMPOTENCY KEY (Chống người dùng bấm nút chuyển tiền 2 lần liên tục)
        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findById(idempotencyKeyStr);
        if (existingKey.isPresent()) {
            return "Giao dịch này đã được ghi nhận trước đó! Bỏ qua để tránh trừ tiền 2 lần.";
        }

        // Đánh dấu Key này đã được xài
        IdempotencyKey newKey = IdempotencyKey.builder()
                .idempotencyKey(idempotencyKeyStr)
                .requestPath("/api/v1/transfers")
                .build();
        idempotencyKeyRepository.save(newKey);

        // 2. GHI NHẬN GIAO DỊCH (Trạng thái PENDING)
        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .currency("VND")
                .status("PENDING")
                .description("Chuyển tiền từ " + fromAccount + " sang " + toAccount)
                .build();
        transfer = transferRepository.save(transfer);

        // 3. LƯU SỰ KIỆN VÀO OUTBOX BẢNG (Đảm bảo Transactional chung với lệnh save Transfer)
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID().toString());
        event.setAggregateType("Transfer");
        event.setAggregateId(transfer.getId().toString());
        event.setType("TransferCreated");
        // Payload có thể là chuỗi JSON chứa thông tin chi tiết. Để đơn giản ta nhét 1 câu thông báo.
        event.setPayload(String.format("{\"transferId\":\"%s\", \"from\":\"%s\", \"to\":\"%s\", \"amount\":%s}", 
                transfer.getId(), fromAccount, toAccount, amount));
        outboxEventRepository.save(event);

        // 4. GỌI SANG ACCOUNT SERVICE ĐỂ ĐÓNG BĂNG TIỀN (Giao tiếp HTTP đồng bộ)
        try {
            // Thực thi RPC qua OpenFeign
            ApiResponse<String> holdResponse = accountClient.holdMoney(fromAccount, amount);
            
            if (holdResponse.isSuccess()) {
                // Tạm thời dừng ở trạng thái PENDING. 
                // Thực tế phải gọi tiếp clear(from) và credit(to), nhưng mình sẽ nâng cấp sau bằng Kafka.
                return "Đã yêu cầu Account Service đóng băng tiền thành công! Giao dịch đang chờ xử lý.";
            } else {
                transfer.setStatus("FAILED");
                transferRepository.save(transfer);
                throw new BusinessException("Account Service từ chối: " + holdResponse.getMessage());
            }
        } catch (Exception e) {
            // Lỗi khi rớt mạng hoặc Account Service bị sập
            transfer.setStatus("FAILED");
            transferRepository.save(transfer);
            throw new BusinessException("Lỗi kết nối đến Account Service (Rớt mạng/Timeout): " + e.getMessage());
        }
    }
}
