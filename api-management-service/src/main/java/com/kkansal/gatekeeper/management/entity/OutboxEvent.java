package com.kkansal.gatekeeper.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(indexes = @Index(name = "idx_outbox_event_created_at", columnList = "created_at"))
public class OutboxEvent extends Auditable {

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, length = Length.LONG32)
    private String payload;

}
