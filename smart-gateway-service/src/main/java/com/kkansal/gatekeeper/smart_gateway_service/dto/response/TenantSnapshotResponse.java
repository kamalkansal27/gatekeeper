package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TenantSnapshotResponse(
        String slug,
        String status,
        @JsonProperty("upstreamSnapshotResponseList") List<UpstreamSnapshotResponse> upstreams) {

    public TenantSnapshotResponse {
        upstreams = upstreams == null ? List.of() : List.copyOf(upstreams);
    }
}
