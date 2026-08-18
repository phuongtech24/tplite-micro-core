package com.tplite.banking.accountservice.service.impl;

import com.tplite.banking.accountservice.entity.Account;
import com.tplite.banking.accountservice.entity.TransactionEntry;
import com.tplite.banking.accountservice.enums.AccountStatus;
import com.tplite.banking.accountservice.enums.TransactionType;
import com.tplite.banking.accountservice.repository.AccountRepository;
import com.tplite.banking.accountservice.repository.TransactionEntryRepository;
import com.tplite.banking.accountservice.service.AccountService;
import com.tplite.banking.accountservice.service.JournalService;
import com.tplite.banking.accountservice.dto.JournalEntryDto;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.math.BigDecimal;
import java.util.Arrays;

import com.tplite.banking.accountservice.util.AccountNumberGenerator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository accountRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final JournalService journalService;

    // Hằng số System GL Accounts
    private static final String SUSPENSE_ACCOUNT = "299000";

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
        // SAGA STEP 1: Khách hàng chuyển tiền đi
        // Ghi Nợ (Trừ) Khách hàng, Ghi Có (Cộng) Tài khoản Treo (299000)
        journalService.postJournal(referenceId, "Chuyển tiền vào tài khoản treo chờ xử lý", Arrays.asList(
                JournalEntryDto.builder()
                        .accountNumber(accountNumber)
                        .type(TransactionType.DEBIT)
                        .amount(amount)
                        .build(),
                JournalEntryDto.builder()
                        .accountNumber(SUSPENSE_ACCOUNT)
                        .type(TransactionType.CREDIT)
                        .amount(amount)
                        .build()
        ));
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void clearMoney(String accountNumber, BigDecimal amount, String referenceId) {
        // HÀM NÀY KHÔNG CÒN Ý NGHĨA TRONG MULTI-LEG VÌ TIỀN ĐÃ NẰM Ở TÀI KHOẢN TREO.
        // Khi creditMoney thành công thì tiền từ Treo đã chạy thẳng sang người nhận.
        // Tuy nhiên để không break SAGA Transfer, ta tạm để log ở đây hoặc xóa gọi bên kia.
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void releaseMoney(String accountNumber, BigDecimal amount, String referenceId) {
        // SAGA COMPENSATING: Giao dịch lỗi
        // Bút toán Đảo (Reversal): Nợ Tài khoản Treo, Có Khách hàng (Trả tiền lại)
        journalService.postJournal(referenceId, "Hoàn tiền giao dịch lỗi (Bút toán đảo)", Arrays.asList(
                JournalEntryDto.builder()
                        .accountNumber(SUSPENSE_ACCOUNT)
                        .type(TransactionType.REVERSAL_DEBIT)
                        .amount(amount)
                        .build(),
                JournalEntryDto.builder()
                        .accountNumber(accountNumber)
                        .type(TransactionType.REVERSAL_CREDIT)
                        .amount(amount)
                        .build()
        ));
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void creditMoney(String accountNumber, BigDecimal amount, String referenceId) {
        // SAGA STEP 2 (Thành công): Cộng tiền cho người nhận
        // Ghi Nợ (Trừ) Tài khoản Treo, Ghi Có (Cộng) Khách hàng nhận
        journalService.postJournal(referenceId, "Nhận tiền chuyển khoản", Arrays.asList(
                JournalEntryDto.builder()
                        .accountNumber(SUSPENSE_ACCOUNT)
                        .type(TransactionType.DEBIT)
                        .amount(amount)
                        .build(),
                JournalEntryDto.builder()
                        .accountNumber(accountNumber)
                        .type(TransactionType.CREDIT)
                        .amount(amount)
                        .build()
        ));
    }

    @Transactional
    @CacheEvict(value = "account", key = "#accountNumber")
    public void deductMoney(String accountNumber, BigDecimal amount, String referenceId) {
        // Ghi Nợ trực tiếp (Ít dùng, trừ thanh toán)
        // Nếu làm chuẩn Multi-leg thì phải truyền thêm Credit account.
        // Tạm thời fix cứng là Trừ Ví, Cộng Tài khoản phí (để API chạy được)
        journalService.postJournal(referenceId, "Trừ tiền trực tiếp", Arrays.asList(
                JournalEntryDto.builder()
                        .accountNumber(accountNumber)
                        .type(TransactionType.DEBIT)
                        .amount(amount)
                        .build(),
                JournalEntryDto.builder()
                        .accountNumber("701000") // Tiền chạy tạm vào Doanh Thu Phí
                        .type(TransactionType.CREDIT)
                        .amount(amount)
                        .build()
        ));
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
