package com.kkansal.gatekeeper.management.service.auth;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class TenantAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final TenantSecurityService tenantSecurityService;

    public TenantAuthorizationManager(TenantSecurityService tenantSecurityService) {
        this.tenantSecurityService = tenantSecurityService;
    }

    @Override
    public AuthorizationResult authorize(Supplier<? extends Authentication> authentication,
                                         RequestAuthorizationContext context) {
        UUID tenantId;
        try {
            tenantId = UUID.fromString(context.getVariables().get("tenantId"));
        } catch (IllegalArgumentException | NullPointerException e) {
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(tenantSecurityService.canAccessTenant(authentication.get(), tenantId));
    }
}
