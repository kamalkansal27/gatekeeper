package com.kkansal.gatekeeper.smart_gateway_service.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record UpstreamNode(
        UUID id,
        long version,
        UUID tenantId,
        String slug,
        String host,
        int port,
        String status,
        Map<UUID, RouteNode> routesById) {

    /** Returns a copy with the route replaced, added, or removed when {@code updated} is null. */
    public UpstreamNode replaceRoute(UUID routeId, RouteNode updated) {
        // Insertion order is kept because route matching returns the first matching pattern.
        Map<UUID, RouteNode> routes = new LinkedHashMap<>(routesById);
        if (updated == null) {
            routes.remove(routeId);
        } else {
            routes.put(routeId, updated);
        }
        return new UpstreamNode(id, version, tenantId, slug, host, port, status, Collections.unmodifiableMap(routes));
    }
}
