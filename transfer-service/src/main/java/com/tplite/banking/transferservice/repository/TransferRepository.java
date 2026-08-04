package com.tplite.banking.transferservice.repository;

import com.tplite.banking.transferservice.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
