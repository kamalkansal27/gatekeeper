package com.kkansal.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_username_tenant",
                columnNames = {"username", "tenant_id"}
        )
)
public class User extends Auditable {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roleSet = new HashSet<>();
}
