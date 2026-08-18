package com.tplite.banking.accountservice.service;

import com.tplite.banking.accountservice.dto.JournalEntryDto;
import com.tplite.banking.accountservice.entity.Account;
import com.tplite.banking.accountservice.entity.TransactionEntry;
import com.tplite.banking.accountservice.enums.TransactionType;
import com.tplite.banking.accountservice.repository.AccountRepository;
import com.tplite.banking.accountservice.repository.TransactionEntryRepository;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final AccountRepository accountRepository;
    private final TransactionEntryRepository transactionEntryRepository;

    @Transactional
    public void postJournal(String referenceId, String description, List<JournalEntryDto> legs) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        // 1. Kiểm tra cân bằng (Nợ = Có)
        for (JournalEntryDto leg : legs) {
            if (leg.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.INVALID_AMOUNT);
            }
            if (leg.getType() == TransactionType.DEBIT || leg.getType() == TransactionType.REVERSAL_DEBIT) {
                totalDebit = totalDebit.add(leg.getAmount());
            } else if (leg.getType() == TransactionType.CREDIT || leg.getType() == TransactionType.REVERSAL_CREDIT) {
                totalCredit = totalCredit.add(leg.getAmount());
            }
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            log.error("Bút toán không cân bằng! Tổng Nợ: {}, Tổng Có: {}", totalDebit, totalCredit);
            throw new BusinessException(ErrorCode.INVALID_AMOUNT); // Tạm dùng mã lỗi này hoặc mã lỗi mới
        }

        // 2. Thực thi hạch toán
        for (JournalEntryDto leg : legs) {
            Account account = accountRepository.findByAccountNumberForUpdate(leg.getAccountNumber())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

            // Đối với mọi loại tài khoản trong hệ thống này:
            // CREDIT = Tăng số dư (Dòng tiền vào ví/két/treo)
            // DEBIT = Giảm số dư (Dòng tiền ra khỏi ví/két/treo)
            if (leg.getType() == TransactionType.CREDIT || leg.getType() == TransactionType.REVERSAL_CREDIT) {
                account.setBalance(account.getBalance().add(leg.getAmount()));
            } else if (leg.getType() == TransactionType.DEBIT || leg.getType() == TransactionType.REVERSAL_DEBIT) {
                if (account.getBalance().compareTo(leg.getAmount()) < 0) {
                    log.error("Tài khoản {} không đủ số dư để ghi Nợ số tiền {}", leg.getAccountNumber(), leg.getAmount());
                    throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
                }
                account.setBalance(account.getBalance().subtract(leg.getAmount()));
            }

            account = accountRepository.save(account);

            // 3. Ghi Sổ cái (Append-only Ledger)
            TransactionEntry entry = TransactionEntry.builder()
                    .accountNumber(account.getAccountNumber())
                    .type(leg.getType())
                    .amount(leg.getAmount())
                    .balanceAfter(account.getBalance()) // Audit trail
                    .referenceId(referenceId)
                    .description(description)
                    .build();
            transactionEntryRepository.save(entry);
        }
        
        log.info("Đã hạch toán thành công Journal có referenceId: {}, Tổng giá trị: {}", referenceId, totalDebit);
    }
}
