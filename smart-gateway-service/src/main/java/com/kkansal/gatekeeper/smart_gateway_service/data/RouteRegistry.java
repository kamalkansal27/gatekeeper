package com.kkansal.gatekeeper.smart_gateway_service.data;

import com.kkansal.gatekeeper.smart_gateway_service.dto.response.RouteSnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.TenantSnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.UpstreamSnapshotResponse;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RouteRegistry {

    private Map<String, List<RouteEntry>> routeMap =  new HashMap<>();

    public void load(SnapshotResponse snapshotResponse) {

        List<TenantSnapshotResponse> tenantSnapshotResponseList = snapshotResponse.tenants();

        for (TenantSnapshotResponse tenant : tenantSnapshotResponseList) {

            String tenantSlug = tenant.slug();
            List<UpstreamSnapshotResponse> upstreamSnapshotResponseList = tenant.upstreams();

            for(UpstreamSnapshotResponse upstream : upstreamSnapshotResponseList) {

                String upstreamSlug = upstream.slug();
                List<RouteSnapshotResponse> routeSnapshotResponseList = upstream.routes();

                for(RouteSnapshotResponse route : routeSnapshotResponseList) {

                    String status = "ACTIVE";
                    String key = tenantSlug + "/" + upstreamSlug;

                    if(Objects.equals(tenant.status(), "INACTIVE") || Objects.equals(upstream.status(), "INACTIVE")) {
                        status = "INACTIVE";
                    }

                    RouteEntry routeEntry = new RouteEntry(
                            upstream.host(),
                            upstream.port(),
                            status,
                            route.httpMethod(),
                            route.registeredPath(),
                            route.targetPath()
                    );


                    routeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(routeEntry);
                }
            }
        }
    }

    public List<RouteEntry> find(String tenantSlug, String upstreamSlug) {

        String key = tenantSlug + "/" + upstreamSlug;
        return routeMap.get(key);
    }
}
