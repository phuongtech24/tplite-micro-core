package com.tplite.banking.accountservice.service;

import com.tplite.banking.accountservice.entity.Account;
import com.tplite.banking.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;

    @Transactional
    public void holdMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.hold(amount);
        accountRepository.save(account);
    }

    @Transactional
    public void clearMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.clear(amount);
        accountRepository.save(account);
    }

    @Transactional
    public void releaseMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.release(amount);
        accountRepository.save(account);
    }

    @Transactional
    public void creditMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.credit(amount);
        accountRepository.save(account);
    }
}
