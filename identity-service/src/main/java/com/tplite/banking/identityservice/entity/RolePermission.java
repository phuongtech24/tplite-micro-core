package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.tplite.banking.common.entity.BaseEntity;

@Entity
@Data
@Table(name = "role_permissions")
public class RolePermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}
