package com.kkansal.gatekeeper.smart_gateway_service.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record TenantNode(
        UUID id,
        long version,
        String slug,
        String status,
        Map<String, UpstreamNode> upstreamsBySlug) {

    public UpstreamNode findUpstream(UUID upstreamId) {
        return upstreamsBySlug.values().stream()
                .filter(upstream -> upstream.id().equals(upstreamId))
                .findFirst()
                .orElse(null);
    }

    /** Returns a copy with {@code old} removed and {@code updated} added; either may be null. */
    public TenantNode replaceUpstream(UpstreamNode old, UpstreamNode updated) {
        Map<String, UpstreamNode> upstreams = new HashMap<>(upstreamsBySlug);
        if (old != null) {
            upstreams.remove(old.slug());
        }
        if (updated != null) {
            upstreams.put(updated.slug(), updated);
        }
        return new TenantNode(id, version, slug, status, Map.copyOf(upstreams));
    }
}
