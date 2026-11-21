package com.kkansal.gatekeeper.management.event;

import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import com.kkansal.gatekeeper.management.entity.enums.Status;

import java.util.UUID;

/**
 * The IDs locate the entity in the tenant → upstream → route tree; the payload carries only
 * the entity's own fields and is null for DELETE. A delete carries the entity's last version.
 */
public record ApiDataChangeEvent(
        UUID tenantId,
        UUID upstreamId,
        UUID routeId,
        EntityType entityType,
        Operation operation,
        long version,
        Object payload) {

    public enum EntityType { TENANT, UPSTREAM, ROUTE }

    public enum Operation { UPSERT, DELETE }

    public record TenantPayload(String slug, Status status) {
    }

    public record UpstreamPayload(String slug, String host, int port, Status status) {
    }

    public record RoutePayload(HttpMethod httpMethod, String registeredPath, String targetPath) {
    }

    public static ApiDataChangeEvent upsert(Tenant tenant) {
        return tenantEvent(tenant, Operation.UPSERT, new TenantPayload(tenant.getSlug(), tenant.getTenantStatus()));
    }

    public static ApiDataChangeEvent delete(Tenant tenant) {
        return tenantEvent(tenant, Operation.DELETE, null);
    }

    public static ApiDataChangeEvent upsert(Upstream upstream) {
        return upstreamEvent(upstream, Operation.UPSERT, new UpstreamPayload(
                upstream.getSlug(), upstream.getHost(), upstream.getPort(), upstream.getServiceStatus()));
    }

    public static ApiDataChangeEvent delete(Upstream upstream) {
        return upstreamEvent(upstream, Operation.DELETE, null);
    }

    public static ApiDataChangeEvent upsert(Route route) {
        return routeEvent(route, Operation.UPSERT, new RoutePayload(
                route.getHttpMethod(), route.getRegisteredPath(), route.getTargetPath()));
    }

    public static ApiDataChangeEvent delete(Route route) {
        return routeEvent(route, Operation.DELETE, null);
    }

    private static ApiDataChangeEvent tenantEvent(Tenant tenant, Operation operation, Object payload) {
        return new ApiDataChangeEvent(tenant.getId(), null, null,
                EntityType.TENANT, operation, tenant.getVersion(), payload);
    }

    private static ApiDataChangeEvent upstreamEvent(Upstream upstream, Operation operation, Object payload) {
        return new ApiDataChangeEvent(upstream.getTenant().getId(), upstream.getId(), null,
                EntityType.UPSTREAM, operation, upstream.getVersion(), payload);
    }

    private static ApiDataChangeEvent routeEvent(Route route, Operation operation, Object payload) {
        Upstream upstream = route.getUpstream();
        return new ApiDataChangeEvent(upstream.getTenant().getId(), upstream.getId(), route.getId(),
                EntityType.ROUTE, operation, route.getVersion(), payload);
    }
}
