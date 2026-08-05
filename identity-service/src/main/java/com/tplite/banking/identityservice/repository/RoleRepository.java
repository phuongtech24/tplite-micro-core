package com.tplite.banking.identityservice.repository;

import com.tplite.banking.identityservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.tplite.banking.identityservice.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
