package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import java.util.UUID;

public record RouteSnapshotResponse(
        UUID id,
        long version,
        String httpMethod,
        String registeredPath,
        String targetPath) {
}
