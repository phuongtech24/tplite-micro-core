package com.tplite.banking.transferservice.service.impl;

import com.tplite.banking.common.dto.ApiResponse;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.transferservice.enums.TransferStatus;
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

        // 2. GỌI SANG ACCOUNT SERVICE ĐỂ TRỪ TIỀN (Giao tiếp HTTP đồng bộ - SAGA Step 1)
        try {
            ApiResponse<String> deductResponse = accountClient.deductMoney(fromAccount, amount);
            if (!deductResponse.isSuccess()) {
                throw new BusinessException("Account Service từ chối: " + deductResponse.getMessage());
            }
        } catch (Exception e) {
            throw new BusinessException("Lỗi trừ tiền (Số dư không đủ hoặc rớt mạng): " + e.getMessage());
        }

        // 3. GHI NHẬN GIAO DỊCH (Trạng thái PROCESSING)
        Transfer transfer = Transfer.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .currency("VND")
                .status(TransferStatus.PROCESSING)
                .description("Chuyển tiền từ " + fromAccount + " sang " + toAccount)
                .build();
        transfer = transferRepository.save(transfer);

        // 4. LƯU SỰ KIỆN VÀO OUTBOX BẢNG ĐỂ KÍCH HOẠT CỘNG TIỀN
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID().toString());
        event.setAggregateType("Transfer");
        event.setAggregateId(transfer.getId().toString());
        event.setType("credit-requested");
        event.setPayload(String.format("{\"transferId\":\"%s\", \"toAccount\":\"%s\", \"amount\":%s}", 
                transfer.getId(), toAccount, amount));
        outboxEventRepository.save(event);

        return "Giao dịch đang được xử lý (SAGA Step 1 thành công)!";
    }
}
