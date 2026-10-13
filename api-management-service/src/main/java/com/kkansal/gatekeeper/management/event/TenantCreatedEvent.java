package com.kkansal.gatekeeper.management.event;

import com.kkansal.gatekeeper.management.entity.Tenant;

public record TenantCreatedEvent(Tenant tenant) {
}
