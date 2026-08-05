package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

import com.tplite.banking.common.entity.BaseEntity;

@Entity
@Data
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;
}
