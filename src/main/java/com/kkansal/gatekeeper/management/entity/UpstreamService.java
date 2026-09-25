package com.kkansal.gatekeeper.management.entity;

import com.kkansal.gatekeeper.management.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class UpstreamService extends Auditable {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
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
