package com.kkansal.gatekeeper.management.service.auth;

import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Grants access when the caller belongs to the tenant named by the {@code {tenantId}} path variable.
 * Routes must use {@link #with(String...)} so the tenant check is always paired with a permission check.
 */
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

        return new AuthorizationDecision(tenantSecurityService.belongsToTenant(authentication.get(), tenantId));
    }

    /**
     * Allows platform admins, or members of the path's tenant holding any of the given permissions.
     */
    public AuthorizationManager<RequestAuthorizationContext> with(String... permissions) {
        return AuthorizationManagers.anyOf(
                AuthorityAuthorizationManager.<RequestAuthorizationContext>hasRole("ADMIN"),
                AuthorizationManagers.allOf(
                        this,
                        AuthorityAuthorizationManager.<RequestAuthorizationContext>hasAnyAuthority(permissions)));
    }
}
