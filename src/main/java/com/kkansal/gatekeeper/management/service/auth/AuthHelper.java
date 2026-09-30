package com.kkansal.gatekeeper.management.service.auth;

import org.springframework.security.core.Authentication;

import java.util.Objects;

public class AuthHelper {

    public static boolean containRole(Authentication authentication, String roleName) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), roleName));
    }

    public static boolean isServiceUser(Authentication authentication) {
        return containRole(authentication, "ROLE_SVC_USER");
    }

    public static boolean isTenantUser(Authentication authentication) {
        return containRole(authentication, "ROLE_TENANT_USER");
    }
}
