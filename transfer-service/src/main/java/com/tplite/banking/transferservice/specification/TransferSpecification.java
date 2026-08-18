package com.tplite.banking.transferservice.specification;

import com.tplite.banking.transferservice.entity.Transfer;
import com.tplite.banking.transferservice.enums.TransferStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferSpecification {

    public static Specification<Transfer> involvesAccount(String accountNumber) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("fromAccount"), accountNumber),
                cb.equal(root.get("toAccount"), accountNumber)
        );
    }

    public static Specification<Transfer> amountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, cb) -> {
            if (minAmount != null && maxAmount != null) {
                return cb.between(root.get("amount"), minAmount, maxAmount);
            } else if (minAmount != null) {
                return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
            } else if (maxAmount != null) {
                return cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Transfer> dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Transfer> statusEquals(TransferStatus status) {
        return (root, query, cb) -> {
            if (status != null) {
                return cb.equal(root.get("status"), status);
            }
            return cb.conjunction();
        };
    }
}
