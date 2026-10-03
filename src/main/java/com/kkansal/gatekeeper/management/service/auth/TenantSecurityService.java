package com.kkansal.gatekeeper.management.service.auth;

import com.kkansal.gatekeeper.management.entity.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TenantSecurityService {

    public boolean belongsToTenant(Authentication authentication, UUID tenantId) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails user
                && user.getTenant() != null
                && Objects.equals(user.getTenant().getId(), tenantId);
    }
}
