package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.tplite.banking.common.entity.BaseEntity;
import com.tplite.banking.identityservice.enums.RoleName;

@Entity
@Data
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name; // ADMIN, CUSTOMER
}
