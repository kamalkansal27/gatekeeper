package com.kkansal.gatekeeper.smart_gateway_service.data;

import java.util.Objects;

public record RouteMatch(TenantNode tenant, UpstreamNode upstream, RouteNode route) {

    public boolean isActive() {
        return !Objects.equals(tenant.status(), "INACTIVE") && !Objects.equals(upstream.status(), "INACTIVE");
    }
}
