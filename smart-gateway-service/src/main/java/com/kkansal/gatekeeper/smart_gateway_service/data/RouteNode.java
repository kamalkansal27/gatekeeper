package com.kkansal.gatekeeper.smart_gateway_service.data;

import java.util.UUID;

public record RouteNode(
        UUID id,
        long version,
        UUID upstreamId,
        String httpMethod,
        String registeredPath,
        String targetPath) {
}
