package com.tplite.banking.accountservice.service;

import java.math.BigDecimal;

import com.tplite.banking.accountservice.entity.Account;

public interface AccountService {
    String createAccount(java.util.UUID userId);
    Account getAccountByNumber(String accountNumber);
    void holdMoney(String accountNumber, BigDecimal amount, String referenceId);
    void clearMoney(String accountNumber, BigDecimal amount, String referenceId);
    void releaseMoney(String accountNumber, BigDecimal amount, String referenceId);
    void creditMoney(String accountNumber, BigDecimal amount, String referenceId);
    void deductMoney(String accountNumber, BigDecimal amount, String referenceId);
    void updateStatus(String accountNumber, String statusStr);
}
