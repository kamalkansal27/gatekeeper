package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TenantResponse {

    private UUID id;

    private String name;

    private String slug;

    private Status tenantStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static TenantResponse from (Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .slug(tenant.getSlug())
                .tenantStatus(tenant.getTenantStatus())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}
