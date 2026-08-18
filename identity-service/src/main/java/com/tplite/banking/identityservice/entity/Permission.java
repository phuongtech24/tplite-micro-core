package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.tplite.banking.common.entity.BaseEntity;

@Entity
@Data
@Table(name = "permissions")
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ví dụ: CREATE_USER, VIEW_BALANCE
}
