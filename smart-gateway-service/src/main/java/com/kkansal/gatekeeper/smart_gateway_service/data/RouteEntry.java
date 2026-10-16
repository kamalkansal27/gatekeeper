package com.kkansal.gatekeeper.smart_gateway_service.data;

public record RouteEntry (
        String host,
        int port,
        String status,
        String httpMethod,
        String registeredPath,
        String targetPath){
}
