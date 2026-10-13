package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateRouteRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateRouteRequest;
import com.kkansal.gatekeeper.management.dto.response.RouteResponse;
import com.kkansal.gatekeeper.management.service.RouteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tenants/{tenantId}/upstreams/{upstreamId}/routes")
public class RouteController {

    private RouteService routeService;

    @PostMapping
    public RouteResponse create(@PathVariable UUID tenantId, @PathVariable UUID upstreamId,
                                @RequestBody @Valid CreateRouteRequest routeRequest) {
        return routeService.create(tenantId, upstreamId, routeRequest);
    }

    @GetMapping
    public List<RouteResponse> getAllByUpstream(@PathVariable UUID tenantId, @PathVariable UUID upstreamId) {
        return routeService.getAllByUpstream(tenantId, upstreamId);
    }

    @GetMapping("/{routeId}")
    public RouteResponse get(@PathVariable UUID tenantId, @PathVariable UUID upstreamId, @PathVariable UUID routeId) {
        return routeService.get(tenantId, upstreamId, routeId);
    }

    @PatchMapping("/{routeId}")
    public RouteResponse update(@PathVariable UUID tenantId, @PathVariable UUID upstreamId, @PathVariable UUID routeId,
                                @RequestBody @Valid UpdateRouteRequest routeRequest) {
        return routeService.update(tenantId, upstreamId, routeId, routeRequest);
    }

    @DeleteMapping("/{routeId}")
    public void delete(@PathVariable UUID tenantId, @PathVariable UUID upstreamId, @PathVariable UUID routeId) {
        routeService.delete(tenantId, upstreamId, routeId);
    }
}
