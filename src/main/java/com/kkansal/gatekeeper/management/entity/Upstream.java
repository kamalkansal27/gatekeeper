package com.kkansal.gatekeeper.management.entity;

import com.kkansal.gatekeeper.management.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Upstream extends Auditable {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status serviceStatus;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

}
