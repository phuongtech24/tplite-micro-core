package com.tplite.banking.accountservice.service.impl;

import com.tplite.banking.accountservice.entity.Account;
import com.tplite.banking.accountservice.enums.AccountStatus;
import com.tplite.banking.accountservice.repository.AccountRepository;
import com.tplite.banking.accountservice.service.AccountService;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository accountRepository;

    @Override
    @Cacheable(value = "account", key = "#accountNumber")
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void holdMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.hold(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void clearMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.clear(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void releaseMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.release(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void creditMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.credit(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void deductMoney(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.deduct(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void updateStatus(String accountNumber, String statusStr) {
        AccountStatus newStatus = AccountStatus.valueOf(statusStr.toUpperCase());
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(newStatus);
        accountRepository.save(account);
    }
}
