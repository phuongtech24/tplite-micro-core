package com.tplite.banking.transferservice.repository;

import com.tplite.banking.transferservice.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
}
