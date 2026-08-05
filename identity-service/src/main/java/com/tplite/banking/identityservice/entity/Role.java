package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.tplite.banking.common.entity.BaseEntity;

@Entity
@Data
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ví dụ: ADMIN, CUSTOMER
}
