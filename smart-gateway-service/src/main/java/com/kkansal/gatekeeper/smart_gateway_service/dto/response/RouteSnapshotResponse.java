package com.kkansal.gatekeeper.smart_gateway_service.dto.response;

import java.util.UUID;

public record RouteSnapshotResponse(
        UUID id,
        String httpMethod,
        String registeredPath,
        String targetPath) {
}
