package com.tplite.banking.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

import com.tplite.banking.common.entity.BaseEntity;
import com.tplite.banking.accountservice.enums.TransactionType;

@Entity
@Table(name = "transaction_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "reference_id", length = 36)
    private String referenceId;

    @Column(length = 255)
    private String description;
}
