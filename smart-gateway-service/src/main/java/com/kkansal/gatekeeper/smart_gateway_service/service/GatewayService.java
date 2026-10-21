package com.kkansal.gatekeeper.smart_gateway_service.service;

import com.kkansal.gatekeeper.smart_gateway_service.client.UpstreamClient;
import com.kkansal.gatekeeper.smart_gateway_service.data.RouteEntry;
import com.kkansal.gatekeeper.smart_gateway_service.data.RouteRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class GatewayService {

    private final RouteRegistry routeRegistry;
    private final UpstreamClient upstreamClient;

    public GatewayService(RouteRegistry routeRegistry, UpstreamClient upstreamClient) {
        this.routeRegistry = routeRegistry;
        this.upstreamClient = upstreamClient;
    }

    public ResponseEntity<byte[]> forward(String tenantSlug, String upstreamSlug, String remainingPath, HttpServletRequest request) throws IOException {

        RouteEntry routeEntry = routeRegistry.find(tenantSlug, upstreamSlug, request.getMethod(), remainingPath)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Route found"));

        return upstreamClient.forward(routeEntry, request);
    }
}
