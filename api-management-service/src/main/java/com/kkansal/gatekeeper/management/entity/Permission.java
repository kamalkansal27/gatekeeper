package com.kkansal.gatekeeper.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Permission extends Auditable{

    @Column(nullable = false, unique = true)
    private String name;
}
