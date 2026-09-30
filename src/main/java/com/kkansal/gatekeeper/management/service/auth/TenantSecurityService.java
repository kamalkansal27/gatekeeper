package com.kkansal.gatekeeper.management.service.auth;

import com.kkansal.gatekeeper.management.entity.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TenantSecurityService {

    public boolean canAccessTenant(Authentication authentication, UUID tenantId) {

        if (AuthHelper.isServiceUser(authentication)) {
            return true;
        }

        return AuthHelper.isTenantUser(authentication)
                && authentication.getPrincipal() instanceof CustomUserDetails user
                && Objects.equals(user.getTenantId(), tenantId);
    }
}
