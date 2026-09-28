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
public class Role extends Auditable {

    @Column(unique = true, nullable = false)
    private String name;
}
