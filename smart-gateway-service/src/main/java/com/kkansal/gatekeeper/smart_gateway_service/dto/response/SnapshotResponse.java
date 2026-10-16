package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record SnapshotResponse(
        LocalDateTime generatedAt,
        @JsonProperty("tenantSnapshotResponseList") List<TenantSnapshotResponse> tenants) {

    public SnapshotResponse {
        tenants = tenants == null ? List.of() : List.copyOf(tenants);
    }
}
