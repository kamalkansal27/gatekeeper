package com.kkansal.gatekeeper.smart_gateway_service.controller;

import com.kkansal.gatekeeper.smart_gateway_service.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;

@RestController
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @RequestMapping("/{tenantSlug}/{upstreamSlug}/**")
    public ResponseEntity<byte[]> proxy(@PathVariable String tenantSlug,
                                        @PathVariable String upstreamSlug,
                                        HttpServletRequest request) throws IOException {

        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String remainingPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, fullPath);
        return gatewayService.forward(tenantSlug, upstreamSlug, remainingPath, request);
    }

}
