package com.tplite.banking.transferservice.service;

import java.math.BigDecimal;

public interface TransferService {
    String createTransfer(String idempotencyKeyStr, String fromAccount, String toAccount, BigDecimal amount);
}
