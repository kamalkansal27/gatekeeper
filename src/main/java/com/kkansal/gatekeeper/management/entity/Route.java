package com.kkansal.gatekeeper.management.entity;

import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Route extends Auditable {

    @Column(nullable = false)
    private String registeredPath;

    @Column(nullable = false)
    private String targetPath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "upstream_service_id", nullable = false)
    private Upstream upstream;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "route_allowed_methods",
            joinColumns = @JoinColumn(name = "route_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "http_method")
    private Set<HttpMethod> allowedMethods;

}
