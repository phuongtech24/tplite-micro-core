package com.tplite.banking.identityservice.repository;

import com.tplite.banking.identityservice.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
