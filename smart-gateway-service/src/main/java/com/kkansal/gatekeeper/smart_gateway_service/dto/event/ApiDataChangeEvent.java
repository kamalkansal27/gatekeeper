package com.kkansal.gatekeeper.smart_gateway_service.dto.event;

import tools.jackson.databind.JsonNode;

import java.util.UUID;

public record ApiDataChangeEvent(
        UUID tenantId,
        UUID upstreamId,
        UUID routeId,
        EntityType entityType,
        Operation operation,
        long version,
        JsonNode payload) {

    public enum EntityType { TENANT, UPSTREAM, ROUTE }

    public enum Operation { UPSERT, DELETE }
}
