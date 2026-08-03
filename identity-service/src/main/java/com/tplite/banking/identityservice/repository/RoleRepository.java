package com.tplite.banking.identityservice.repository;

import com.tplite.banking.identityservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
