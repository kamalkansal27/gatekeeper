package com.kkansal.gatekeeper.smart_gateway_service.data;

import com.kkansal.gatekeeper.smart_gateway_service.dto.event.RoutePayload;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.TenantPayload;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.UpstreamPayload;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.RouteSnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.TenantSnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.UpstreamSnapshotResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.kkansal.gatekeeper.smart_gateway_service.logging.Logger.DATA_CHANGE_LOGGER;

/**
 * Request threads read while updates replace the whole state, so every state is
 * immutable and swapped atomically; readers never see a half-applied change.
 * Writers are synchronized so two updates cannot both build on the same old state.
 */
@Component
public class RouteRegistry {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private record State(Map<String, TenantNode> tenantsBySlug, Map<UUID, TenantNode> tenantsById) {

        /** Returns a copy with {@code old} removed and {@code updated} added; either may be null. */
        State replaceTenant(TenantNode old, TenantNode updated) {
            Map<String, TenantNode> bySlug = new HashMap<>(tenantsBySlug);
            Map<UUID, TenantNode> byId = new HashMap<>(tenantsById);
            if (old != null) {
                bySlug.remove(old.slug());
                byId.remove(old.id());
            }
            if (updated != null) {
                bySlug.put(updated.slug(), updated);
                byId.put(updated.id(), updated);
            }
            return new State(Map.copyOf(bySlug), Map.copyOf(byId));
        }
    }

    private final AtomicReference<State> state = new AtomicReference<>(new State(Map.of(), Map.of()));

    // Last version of every deleted entity, so a stale upsert arriving after the delete cannot resurrect it.
    // Children removed by a parent delete need no entry: their events are ignored once the parent is gone.
    private final Map<UUID, Long> tombstones = new HashMap<>();

    public synchronized void load(SnapshotResponse snapshotResponse) {

        Map<String, TenantNode> tenantsBySlug = new HashMap<>();
        Map<UUID, TenantNode> tenantsById = new HashMap<>();

        for (TenantSnapshotResponse tenant : snapshotResponse.tenants()) {

            Map<String, UpstreamNode> upstreamsBySlug = new HashMap<>();

            for (UpstreamSnapshotResponse upstream : tenant.upstreams()) {

                // Insertion order is kept because route matching returns the first matching pattern.
                Map<UUID, RouteNode> routesById = new LinkedHashMap<>();

                for (RouteSnapshotResponse route : upstream.routes()) {
                    routesById.put(route.id(), new RouteNode(
                            route.id(),
                            route.version(),
                            upstream.id(),
                            route.httpMethod(),
                            route.registeredPath(),
                            route.targetPath()));
                }

                UpstreamNode upstreamNode = new UpstreamNode(
                        upstream.id(),
                        upstream.version(),
                        tenant.id(),
                        upstream.slug(),
                        upstream.host(),
                        upstream.port(),
                        upstream.status(),
                        Collections.unmodifiableMap(routesById));

                upstreamsBySlug.put(upstreamNode.slug(), upstreamNode);
            }

            TenantNode tenantNode = new TenantNode(tenant.id(), tenant.version(), tenant.slug(), tenant.status(), Map.copyOf(upstreamsBySlug));

            tenantsBySlug.put(tenantNode.slug(), tenantNode);
            tenantsById.put(tenantNode.id(), tenantNode);
        }

        state.set(new State(Map.copyOf(tenantsBySlug), Map.copyOf(tenantsById)));
    }

    public Optional<RouteMatch> find(String tenantSlug, String upstreamSlug, String httpMethod, String remainingPath) {

        TenantNode tenant = state.get().tenantsBySlug().get(tenantSlug);
        if (tenant == null) {
            return Optional.empty();
        }

        UpstreamNode upstream = tenant.upstreamsBySlug().get(upstreamSlug);
        if (upstream == null) {
            return Optional.empty();
        }

        return upstream.routesById().values().stream()
                .filter(r -> r.httpMethod().equalsIgnoreCase(httpMethod))
                .filter(r -> PATH_MATCHER.match(r.registeredPath(), remainingPath))
                .findFirst()
                .map(route -> new RouteMatch(tenant, upstream, route));
    }

    public synchronized void upsertTenant(UUID tenantId, long version, TenantPayload payload) {

        State current = state.get();
        TenantNode existing = current.tenantsById().get(tenantId);
        if (isStaleUpsert(tenantId, existing == null ? null : existing.version(), version)) {
            return;
        }

        Map<String, UpstreamNode> upstreams = existing == null ? Map.of() : existing.upstreamsBySlug();
        TenantNode updated = new TenantNode(tenantId, version, payload.slug(), payload.status(), upstreams);

        state.set(current.replaceTenant(existing, updated));
    }

    public synchronized void deleteTenant(UUID tenantId, long version) {

        State current = state.get();
        TenantNode existing = current.tenantsById().get(tenantId);
        if (!recordDelete(tenantId, existing == null ? null : existing.version(), version)) {
            return;
        }

        state.set(current.replaceTenant(existing, null));
    }

    public synchronized void upsertUpstream(UUID tenantId, UUID upstreamId, long version, UpstreamPayload payload) {

        State current = state.get();
        TenantNode tenant = findParentTenant(current, tenantId, upstreamId);
        if (tenant == null) {
            return;
        }

        UpstreamNode existing = tenant.findUpstream(upstreamId);
        if (isStaleUpsert(upstreamId, existing == null ? null : existing.version(), version)) {
            return;
        }

        Map<UUID, RouteNode> routes = existing == null ? Map.of() : existing.routesById();
        UpstreamNode updated = new UpstreamNode(upstreamId, version, tenantId,
                payload.slug(), payload.host(), payload.port(), payload.status(), routes);

        state.set(current.replaceTenant(tenant, tenant.replaceUpstream(existing, updated)));
    }

    public synchronized void deleteUpstream(UUID tenantId, UUID upstreamId, long version) {

        State current = state.get();
        TenantNode tenant = current.tenantsById().get(tenantId);
        UpstreamNode existing = tenant == null ? null : tenant.findUpstream(upstreamId);
        if (!recordDelete(upstreamId, existing == null ? null : existing.version(), version) || existing == null) {
            return;
        }

        state.set(current.replaceTenant(tenant, tenant.replaceUpstream(existing, null)));
    }

    public synchronized void upsertRoute(UUID tenantId, UUID upstreamId, UUID routeId, long version, RoutePayload payload) {

        State current = state.get();
        TenantNode tenant = findParentTenant(current, tenantId, routeId);
        UpstreamNode upstream = tenant == null ? null : findParentUpstream(tenant, upstreamId, routeId);
        if (upstream == null) {
            return;
        }

        RouteNode existing = upstream.routesById().get(routeId);
        if (isStaleUpsert(routeId, existing == null ? null : existing.version(), version)) {
            return;
        }

        RouteNode updated = new RouteNode(routeId, version, upstreamId,
                payload.httpMethod(), payload.registeredPath(), payload.targetPath());

        state.set(current.replaceTenant(tenant, tenant.replaceUpstream(upstream, upstream.replaceRoute(routeId, updated))));
    }

    public synchronized void deleteRoute(UUID tenantId, UUID upstreamId, UUID routeId, long version) {

        State current = state.get();
        TenantNode tenant = current.tenantsById().get(tenantId);
        UpstreamNode upstream = tenant == null ? null : tenant.findUpstream(upstreamId);
        RouteNode existing = upstream == null ? null : upstream.routesById().get(routeId);
        if (!recordDelete(routeId, existing == null ? null : existing.version(), version) || existing == null) {
            return;
        }

        state.set(current.replaceTenant(tenant, tenant.replaceUpstream(upstream, upstream.replaceRoute(routeId, null))));
    }

    private boolean isStaleUpsert(UUID id, Long currentVersion, long version) {

        long latest = Math.max(currentVersion == null ? -1 : currentVersion, tombstones.getOrDefault(id, -1L));
        if (version <= latest) {
            DATA_CHANGE_LOGGER.debug("Skipping stale upsert of {} at version {} (latest known {})", id, version, latest);
            return true;
        }
        return false;
    }

    /** Records the tombstone and returns whether the delete should be applied to the tree. */
    private boolean recordDelete(UUID id, Long currentVersion, long version) {

        // A delete carries the entity's last version, so it only loses to a strictly newer state.
        if (currentVersion != null && currentVersion > version) {
            DATA_CHANGE_LOGGER.debug("Skipping stale delete of {} at version {} (current {})", id, version, currentVersion);
            return false;
        }
        tombstones.merge(id, version, Math::max);
        return true;
    }

    // A missing parent means it was deleted, or this event is older than it; either way the child is unreachable.
    private TenantNode findParentTenant(State current, UUID tenantId, UUID childId) {

        TenantNode tenant = current.tenantsById().get(tenantId);
        if (tenant == null) {
            DATA_CHANGE_LOGGER.debug("Ignoring change to {}: tenant {} is not known", childId, tenantId);
        }
        return tenant;
    }

    private UpstreamNode findParentUpstream(TenantNode tenant, UUID upstreamId, UUID routeId) {

        UpstreamNode upstream = tenant.findUpstream(upstreamId);
        if (upstream == null) {
            DATA_CHANGE_LOGGER.debug("Ignoring change to {}: upstream {} is not known", routeId, upstreamId);
        }
        return upstream;
    }
}
