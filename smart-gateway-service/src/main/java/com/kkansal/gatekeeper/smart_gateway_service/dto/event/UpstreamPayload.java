package com.kkansal.gatekeeper.smart_gateway_service.dto.event;

public record UpstreamPayload(String slug, String host, int port, String status) {
}
