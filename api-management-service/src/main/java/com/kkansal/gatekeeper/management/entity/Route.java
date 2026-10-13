package com.kkansal.gatekeeper.management.entity;

import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_route_upstream_path_method",
        columnNames = {"upstream_service_id", "registered_path", "http_method"}
))
public class Route extends Auditable {

    @Column(nullable = false)
    private String registeredPath;

    @Column(nullable = false)
    private String targetPath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "upstream_service_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Upstream upstream;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod httpMethod;

}
