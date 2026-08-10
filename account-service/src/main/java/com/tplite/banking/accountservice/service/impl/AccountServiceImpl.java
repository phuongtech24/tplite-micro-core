package com.tplite.banking.accountservice.service.impl;

import com.tplite.banking.accountservice.entity.Account;
import com.tplite.banking.accountservice.entity.TransactionEntry;
import com.tplite.banking.accountservice.enums.AccountStatus;
import com.tplite.banking.accountservice.enums.TransactionType;
import com.tplite.banking.accountservice.repository.AccountRepository;
import com.tplite.banking.accountservice.repository.TransactionEntryRepository;
import com.tplite.banking.accountservice.service.AccountService;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.math.BigDecimal;

import com.tplite.banking.accountservice.util.AccountNumberGenerator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository accountRepository;
    private final TransactionEntryRepository transactionEntryRepository;

    @Override
    @Transactional
    public String createAccount(UUID userId) {
        String accountNumber;
        boolean exists;
        // Đảm bảo account number sinh ra là duy nhất (rất khó trùng nhưng vẫn nên check)
        do {
            accountNumber = AccountNumberGenerator.generate();
            exists = accountRepository.findByAccountNumber(accountNumber).isPresent();
        } while (exists);

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .frozenAmount(BigDecimal.ZERO)
                .currency("VND")
                .status(AccountStatus.ACTIVE)
                .build();
                
        accountRepository.save(account);
        return accountNumber;
    }

    @Override
    @Cacheable(value = "account", key = "#accountNumber")
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void holdMoney(String accountNumber, BigDecimal amount, String referenceId) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.hold(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void clearMoney(String accountNumber, BigDecimal amount, String referenceId) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.clear(amount);
        account = accountRepository.save(account);
        
        // Ghi sổ cái: DEBIT (Trừ tiền)
        createTransactionEntry(account, TransactionType.DEBIT, amount, referenceId, "Hoàn tất chuyển tiền");
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void releaseMoney(String accountNumber, BigDecimal amount, String referenceId) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.release(amount);
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void creditMoney(String accountNumber, BigDecimal amount, String referenceId) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.credit(amount);
        account = accountRepository.save(account);

        // Ghi sổ cái: CREDIT (Cộng tiền)
        createTransactionEntry(account, TransactionType.CREDIT, amount, referenceId, "Nhận tiền");
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void deductMoney(String accountNumber, BigDecimal amount, String referenceId) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.deduct(amount);
        account = accountRepository.save(account);

        // Ghi sổ cái: DEBIT (Trừ tiền trực tiếp)
        createTransactionEntry(account, TransactionType.DEBIT, amount, referenceId, "Trừ tiền trực tiếp");
    }
    
    private void createTransactionEntry(Account account, TransactionType type, BigDecimal amount, String referenceId, String description) {
        TransactionEntry entry = TransactionEntry.builder()
                .accountNumber(account.getAccountNumber())
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance()) // Audit trail: số dư ngay sau giao dịch
                .referenceId(referenceId)
                .description(description)
                .build();
        transactionEntryRepository.save(entry);
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
