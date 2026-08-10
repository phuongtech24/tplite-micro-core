package com.tplite.banking.accountservice.repository;

import com.tplite.banking.accountservice.entity.TransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionEntryRepository extends JpaRepository<TransactionEntry, UUID> {

    @Query("SELECT COALESCE(SUM(" +
           "CASE " +
           "  WHEN t.type = 'CREDIT' THEN t.amount " +
           "  WHEN t.type = 'REVERSAL_CREDIT' THEN t.amount " +
           "  WHEN t.type = 'DEBIT' THEN -t.amount " +
           "  WHEN t.type = 'REVERSAL_DEBIT' THEN -t.amount " +
           "  ELSE 0 " +
           "END" +
           "), 0) FROM TransactionEntry t WHERE t.accountNumber = :accountNumber")
    BigDecimal calculateActualBalance(@Param("accountNumber") String accountNumber);
}
