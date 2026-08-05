package com.tplite.banking.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.banking.common.entity.BaseEntity;
import com.tplite.banking.accountservice.enums.AccountStatus;
import com.tplite.banking.common.exception.BusinessException;
import com.tplite.banking.common.exception.ErrorCode;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false, length = 30)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "frozen_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal frozenAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

        // Tính số dư khả dụng
    public BigDecimal getAvailableBalance() {
        return balance.subtract(frozenAmount);
    }

    // Đóng băng tiền khi chuẩn bị chuyển
    public void hold(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        if (getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.frozenAmount = this.frozenAmount.add(amount);
    }

    // Xóa sổ (Trừ hẳn tiền khi giao dịch đầu kia báo thành công)
    public void clear(BigDecimal amount) {
        if (this.frozenAmount.compareTo(amount) < 0) {
            throw new IllegalStateException("Lỗi logic: Số tiền đóng băng không đủ để Clear");
        }
        this.frozenAmount = this.frozenAmount.subtract(amount);
        this.balance = this.balance.subtract(amount);
    }

    // Hoàn trả (Nhả tiền đóng băng về lại ví do giao dịch lỗi)
    public void release(BigDecimal amount) {
        if (this.frozenAmount.compareTo(amount) < 0) {
            throw new IllegalStateException("Lỗi logic: Số tiền đóng băng không đủ để Release");
        }
        this.frozenAmount = this.frozenAmount.subtract(amount);
    }

    // Cộng tiền (Khi ai đó chuyển tiền tới)
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        this.balance = this.balance.add(amount);
    }

    // Trừ tiền trực tiếp (Dùng cho SAGA Bước 1)
    public void deduct(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        if (getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance = this.balance.subtract(amount);
    }

}
