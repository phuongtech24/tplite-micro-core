package com.tplite.banking.identityservice.repository;

import com.tplite.banking.identityservice.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleIdIn(List<Long> roleIds);
}
