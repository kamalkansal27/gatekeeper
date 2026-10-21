package com.kkansal.gatekeeper.smart_gateway_service.controller;

import com.kkansal.gatekeeper.smart_gateway_service.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @RequestMapping("/{tenantSlug}/{upstreamSlug}/{remainingPath}")
    public ResponseEntity<byte[]> proxy(@PathVariable String tenantSlug,
                                        @PathVariable String upstreamSlug,
                                        @PathVariable String remainingPath,   // "/users"
                                        HttpServletRequest request) throws IOException {
        return gatewayService.forward(tenantSlug, upstreamSlug, remainingPath, request);
    }

}
