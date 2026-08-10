package com.tplite.banking.accountservice.service;

import java.math.BigDecimal;

import com.tplite.banking.accountservice.entity.Account;

public interface AccountService {
    String createAccount(java.util.UUID userId);
    Account getAccountByNumber(String accountNumber);
    void holdMoney(String accountNumber, BigDecimal amount);
    void clearMoney(String accountNumber, BigDecimal amount);
    void releaseMoney(String accountNumber, BigDecimal amount);
    void creditMoney(String accountNumber, BigDecimal amount);
    void deductMoney(String accountNumber, BigDecimal amount);
    void updateStatus(String accountNumber, String statusStr);
}
