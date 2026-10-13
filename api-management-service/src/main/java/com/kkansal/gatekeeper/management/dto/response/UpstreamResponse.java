package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.entity.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UpstreamResponse {

    private UUID id;

    private String name;

    private String slug;

    private String host;

    private Integer port;

    private Status serviceStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String tenantName;

    private String tenantSlug;

    public static UpstreamResponse from(Upstream upstream) {
        return UpstreamResponse.builder()
                .id(upstream.getId())
                .name(upstream.getName())
                .slug(upstream.getSlug())
                .host(upstream.getHost())
                .port(upstream.getPort())
                .serviceStatus(upstream.getServiceStatus())
                .createdAt(upstream.getCreatedAt())
                .updatedAt(upstream.getUpdatedAt())
                .tenantName(upstream.getTenant().getName())
                .tenantSlug(upstream.getTenant().getSlug())
                .build();
    }
}
