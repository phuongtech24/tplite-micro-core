package com.tplite.banking.transferservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.tplite.banking.common.entity.BaseEntity;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
public class OutboxEvent extends BaseEntity {
    
    @Id
    private String id; // UUID

    private String aggregateType;
    private String aggregateId;
    private String type;
    
    // JSON payload
    private String payload;
}
