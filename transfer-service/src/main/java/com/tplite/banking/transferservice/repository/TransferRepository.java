package com.tplite.banking.transferservice.repository;

import com.tplite.banking.transferservice.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    
    @Query(value = "SELECT * FROM ( " +
                   "  SELECT * FROM transfers WHERE from_account = :accountNumber " +
                   "  UNION ALL " +
                   "  SELECT * FROM transfers WHERE to_account = :accountNumber " +
                   ") AS t ORDER BY t.created_at DESC", 
           countQuery = "SELECT count(*) FROM ( " +
                        "  SELECT id FROM transfers WHERE from_account = :accountNumber " +
                        "  UNION ALL " +
                        "  SELECT id FROM transfers WHERE to_account = :accountNumber " +
                        ") AS t",
           nativeQuery = true)
    Page<Transfer> findTransactionHistory(@Param("accountNumber") String accountNumber, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transfer t WHERE t.fromAccount = :accountNumber AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    long countTransactionsByDateRange(@Param("accountNumber") String accountNumber, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
