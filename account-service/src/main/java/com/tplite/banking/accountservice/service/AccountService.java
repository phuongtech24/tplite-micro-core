package com.tplite.banking.accountservice.service;

import java.math.BigDecimal;

public interface AccountService {
    void holdMoney(String accountNumber, BigDecimal amount);
    void clearMoney(String accountNumber, BigDecimal amount);
    void releaseMoney(String accountNumber, BigDecimal amount);
    void creditMoney(String accountNumber, BigDecimal amount);
}
