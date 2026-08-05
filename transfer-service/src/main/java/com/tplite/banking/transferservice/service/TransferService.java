package com.tplite.banking.transferservice.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

public interface TransferService {
    String createTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount);
    Page<com.tplite.banking.transferservice.entity.Transfer> getTransactionHistory(String accountNumber, int page, int size);
}
