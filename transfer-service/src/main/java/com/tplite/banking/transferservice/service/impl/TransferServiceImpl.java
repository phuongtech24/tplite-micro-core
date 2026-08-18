package com.tplite.banking.transferservice.service.impl;

import com.tplite.banking.common.dto.ApiResponse;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.tplite.banking.transferservice.specification.TransferSpecification;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final AccountClient accountClient; // Vũ khí gọi HTTP sang Account Service

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackCreateTransfer")
    @Transactional
    public String createTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount) {
        
        // 1. KIỂM TRA IDEMPOTENCY KEY (Chống người dùng bấm nút chuyển tiền 2 lần liên tục)
        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findById(idempotencyKeyStr);
        if (existingKey.isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_IDEMPOTENCY);
        }

        // Đánh dấu Key này đã được xài
        IdempotencyKey newKey = IdempotencyKey.builder()
                .idempotencyKey(idempotencyKeyStr)
                .requestPath("/api/v1/transfers")
                .build();
        idempotencyKeyRepository.save(newKey);

        // 2. GỌI SANG ACCOUNT SERVICE ĐỂ ĐÓNG BĂNG TIỀN (SAGA Step 1: HOLD)
        try {
            ApiResponse<String> holdResponse = accountClient.holdMoney(fromAccount, amount, idempotencyKeyStr);
            if (!holdResponse.isSuccess()) {
                throw new BusinessException(ErrorCode.TRANSFER_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TRANSFER_FAILED);
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
        event.setPayload(String.format("{\"transferId\":\"%s\", \"fromAccount\":\"%s\", \"toAccount\":\"%s\", \"amount\":%s}", 
                transfer.getId(), fromAccount, toAccount, amount));
        outboxEventRepository.save(event);

        return "Giao dịch đang được xử lý (Đã đóng băng tiền thành công)!";
    }

    public String fallbackCreateTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount, Throwable t) {
        return "Hệ thống Account Service hiện đang bận hoặc quá tải. Vui lòng thử lại sau ít phút!";
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transfer> getTransactionHistory(String accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transferRepository.findTransactionHistory(accountNumber, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTransactionsByDateRange(String accountNumber, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return transferRepository.countTransactionsByDateRange(accountNumber, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transfer> getTransactionHistoryWithFilter(
            String accountNumber, BigDecimal minAmount, BigDecimal maxAmount, 
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, 
            TransferStatus status, int page, int size) {
        
        Specification<Transfer> spec = Specification.where(TransferSpecification.involvesAccount(accountNumber))
                .and(TransferSpecification.amountBetween(minAmount, maxAmount))
                .and(TransferSpecification.dateBetween(startDate, endDate))
                .and(TransferSpecification.statusEquals(status));
                
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        return transferRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportToCsv(
            String accountNumber, BigDecimal minAmount, BigDecimal maxAmount, 
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, 
            TransferStatus status) {
            
        Specification<Transfer> spec = Specification.where(TransferSpecification.involvesAccount(accountNumber))
                .and(TransferSpecification.amountBetween(minAmount, maxAmount))
                .and(TransferSpecification.dateBetween(startDate, endDate))
                .and(TransferSpecification.statusEquals(status));
                
        // Lấy tất cả dữ liệu thỏa mãn bộ lọc (Không phân trang)
        List<Transfer> transfers = transferRepository.findAll(spec, org.springframework.data.domain.Sort.by("createdAt").descending());
        
        StringBuilder csv = new StringBuilder();
        // Header
        csv.append("ID Giao dich,Nguoi gui,Nguoi nhan,So tien,Tien te,Trang thai,Noi dung,Ngay tao\n");
        
        // Data
        for (Transfer t : transfers) {
            csv.append(t.getId()).append(",")
               .append(t.getFromAccount()).append(",")
               .append(t.getToAccount()).append(",")
               .append(t.getAmount()).append(",")
               .append(t.getCurrency()).append(",")
               .append(t.getStatus()).append(",")
               .append("\"").append(t.getDescription() != null ? t.getDescription().replace("\"", "\"\"") : "").append("\",")
               .append(t.getCreatedAt())
               .append("\n");
        }
        
        return csv.toString();
    }
}
