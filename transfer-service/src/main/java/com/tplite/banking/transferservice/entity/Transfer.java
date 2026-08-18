package com.tplite.banking.transferservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.banking.common.entity.BaseEntity;
import com.tplite.banking.transferservice.enums.TransferStatus;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "from_account", nullable = false, length = 30)
    private String fromAccount;

    @Column(name = "to_account", nullable = false, length = 30)
    private String toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status; // PENDING, SUCCESS, FAILED, ROLLBACKED

    private String description;

}
