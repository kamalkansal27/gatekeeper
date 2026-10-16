package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record UpstreamSnapshotResponse(
        UUID id,
        String slug,
        String status,
        String host,
        Integer port,
        @JsonProperty("routeSnapshotResponseList") List<RouteSnapshotResponse> routes) {

    public UpstreamSnapshotResponse {
        routes = routes == null ? List.of() : List.copyOf(routes);
    }
}
