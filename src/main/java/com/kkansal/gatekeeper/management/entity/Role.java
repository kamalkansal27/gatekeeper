package com.kkansal.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_name_tenant",
                columnNames = {"name", "tenant_id"}
        )
)
public class Role extends Auditable {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissionSet  = new HashSet<>();
}
