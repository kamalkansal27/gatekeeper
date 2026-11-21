package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record TenantSnapshotResponse(
        UUID id,
        long version,
        String slug,
        String status,
        @JsonProperty("upstreamSnapshotResponseList") List<UpstreamSnapshotResponse> upstreams) {

    public TenantSnapshotResponse {
        upstreams = upstreams == null ? List.of() : List.copyOf(upstreams);
    }
}
