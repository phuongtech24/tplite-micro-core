package com.tplite.banking.identityservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

import com.tplite.banking.common.entity.BaseEntity;
import com.tplite.banking.identityservice.enums.EkycStatus;
import com.tplite.banking.identityservice.enums.UserStatus;

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

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "id_card_number")
    private String idCardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "ekyc_status", nullable = false)
    private EkycStatus ekycStatus = EkycStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Version
    private Long version;
}
