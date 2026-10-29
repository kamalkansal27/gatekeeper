package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import com.kkansal.gatekeeper.management.entity.enums.Status;

public record RouteEntry (
        String tenantSlug,
        String upstreamSlug,
        String host,
        int port,
        String status,
        HttpMethod httpMethod,
        String registeredPath,
        String targetPath
) {

    public static RouteEntry from(Route route) {

        Upstream upstream = route.getUpstream();
        boolean inactive = upstream.getTenant().getTenantStatus() == Status.INACTIVE
                || upstream.getServiceStatus() == Status.INACTIVE;

        return new RouteEntry(
                upstream.getTenant().getSlug(),
                upstream.getSlug(),
                upstream.getHost(),
                upstream.getPort(),
                inactive ? Status.INACTIVE.name() : Status.ACTIVE.name(),
                route.getHttpMethod(),
                route.getRegisteredPath(),
                route.getTargetPath()
        );
    }
}
