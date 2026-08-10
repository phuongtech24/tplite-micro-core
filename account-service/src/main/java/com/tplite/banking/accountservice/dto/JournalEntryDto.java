package com.tplite.banking.accountservice.dto;

import com.tplite.banking.accountservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDto {
    private String accountNumber;
    private TransactionType type;
    private BigDecimal amount;
}
