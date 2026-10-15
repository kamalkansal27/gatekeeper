package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.response.RouteSnapshotResponse;
import com.kkansal.gatekeeper.management.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.management.dto.response.TenantSnapshotResponse;
import com.kkansal.gatekeeper.management.dto.response.UpstreamSnapshotResponse;
import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.Upstream;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SnapshotService {

    private TenantService tenantService;
    private RouteService routeService;
    private UpstreamService upstreamService;

    public SnapshotService(TenantService tenantService, RouteService routeService, UpstreamService upstreamService) {
        this.routeService = routeService;
        this.tenantService = tenantService;
        this.upstreamService = upstreamService;
    }

    public SnapshotResponse get() {

        List<Tenant> tenantList = tenantService.findAll();

        List<TenantSnapshotResponse> tenantSnapshotResponseList = new ArrayList<>();

        for(Tenant tenant : tenantList) {

            List<Upstream> upstreamList = upstreamService.findAllByTenant(tenant);
            List<UpstreamSnapshotResponse> upstreamSnapshotResponseList = new ArrayList<>();

            for (Upstream upstream : upstreamList) {

                List<Route> routeList = routeService.findAllByUpstream(upstream);
                List<RouteSnapshotResponse> routeSnapshotResponseList = new ArrayList<>();

                for(Route route : routeList) {
                    routeSnapshotResponseList.add(RouteSnapshotResponse.builder()
                            .id(route.getId())
                            .httpMethod(route.getHttpMethod())
                            .registeredPath(route.getRegisteredPath())
                            .targetPath(route.getTargetPath())
                            .build());
                }

                upstreamSnapshotResponseList.add(UpstreamSnapshotResponse.builder()
                            .id(upstream.getId())
                            .slug(upstream.getSlug())
                            .status(upstream.getServiceStatus())
                            .host(upstream.getHost())
                            .port(upstream.getPort())
                            .routeSnapshotResponseList(routeSnapshotResponseList)
                            .build());
            }

            tenantSnapshotResponseList.add(
                    TenantSnapshotResponse.builder()
                            .slug(tenant.getSlug())
                            .status(tenant.getTenantStatus())
                            .upstreamSnapshotResponseList(upstreamSnapshotResponseList)
                            .build()
            );
        }

        SnapshotResponse snapshotResponse = SnapshotResponse.builder()
                .generatedAt(LocalDateTime.now())
                .tenantSnapshotResponseList(tenantSnapshotResponseList)
                .build();

        return snapshotResponse;
    }
}
