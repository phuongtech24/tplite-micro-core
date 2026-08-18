package com.tplite.banking.identityservice.repository;

import com.tplite.banking.identityservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(UUID userId);
}
