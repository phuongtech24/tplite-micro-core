package com.tplite.banking.transferservice.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

public interface TransferService {
    String createTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount);
    Page<com.tplite.banking.transferservice.entity.Transfer> getTransactionHistory(String accountNumber, int page, int size);
    long countTransactionsByDateRange(String accountNumber, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    Page<com.tplite.banking.transferservice.entity.Transfer> getTransactionHistoryWithFilter(
            String accountNumber, BigDecimal minAmount, BigDecimal maxAmount, 
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, 
            com.tplite.banking.transferservice.enums.TransferStatus status, 
            int page, int size);
            
    String exportToCsv(String accountNumber, BigDecimal minAmount, BigDecimal maxAmount, 
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, 
            com.tplite.banking.transferservice.enums.TransferStatus status);
}
