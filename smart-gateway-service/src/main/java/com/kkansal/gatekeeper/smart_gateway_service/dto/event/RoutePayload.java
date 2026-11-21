package com.kkansal.gatekeeper.smart_gateway_service.dto.event;

public record RoutePayload(String httpMethod, String registeredPath, String targetPath) {
}
