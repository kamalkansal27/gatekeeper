package com.kkansal.gatekeeper.management.config;

import com.kkansal.gatekeeper.management.service.auth.CustomUserDetailsService;
import com.kkansal.gatekeeper.management.service.auth.TenantAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider =  new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   DaoAuthenticationProvider daoAuthenticationProvider,
                                                   TenantAuthorizationManager tenant) {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authenticationProvider(daoAuthenticationProvider)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->

                        auth.requestMatchers("/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Every tenant-scoped route must be listed explicitly; unlisted routes fall through to denyAll().
                        .requestMatchers(GET, "/api/tenants/{tenantId}").access(tenant.with("TENANT_READ", "TENANT_WRITE"))
                        .requestMatchers(PATCH, "/api/tenants/{tenantId}").access(tenant.with("TENANT_WRITE"))

                        .requestMatchers(POST, "/api/tenants/{tenantId}/roles").access(tenant.with("ROLE_WRITE"))
                        .requestMatchers(GET, "/api/tenants/{tenantId}/roles/*").access(tenant.with("ROLE_READ", "ROLE_WRITE"))
                        .requestMatchers(PATCH, "/api/tenants/{tenantId}/roles/*").access(tenant.with("ROLE_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/roles/*").access(tenant.with("ROLE_WRITE"))
                        .requestMatchers(PUT, "/api/tenants/{tenantId}/roles/*/permissions/*").access(tenant.with("ROLE_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/roles/*/permissions/*").access(tenant.with("ROLE_WRITE"))

                        .requestMatchers(GET, "/api/tenants/{tenantId}/upstreams", "/api/tenants/{tenantId}/upstreams/*").access(tenant.with("UPSTREAM_READ", "UPSTREAM_WRITE"))
                        .requestMatchers(POST, "/api/tenants/{tenantId}/upstreams").access(tenant.with("UPSTREAM_WRITE"))
                        .requestMatchers(PATCH, "/api/tenants/{tenantId}/upstreams/*").access(tenant.with("UPSTREAM_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/upstreams/*").access(tenant.with("UPSTREAM_WRITE"))

                        .requestMatchers(GET, "/api/tenants/{tenantId}/upstreams/*/routes", "/api/tenants/{tenantId}/upstreams/*/routes/*").access(tenant.with("ROUTE_READ", "ROUTE_WRITE"))
                        .requestMatchers(POST, "/api/tenants/{tenantId}/upstreams/*/routes").access(tenant.with("ROUTE_WRITE"))
                        .requestMatchers(PATCH, "/api/tenants/{tenantId}/upstreams/*/routes/*").access(tenant.with("ROUTE_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/upstreams/*/routes/*").access(tenant.with("ROUTE_WRITE"))

                        .requestMatchers(POST, "/api/tenants/{tenantId}/users/register").access(tenant.with("USER_WRITE"))
                        .requestMatchers(GET, "/api/tenants/{tenantId}/users/*", "/api/tenants/{tenantId}/users/*/roles").access(tenant.with("USER_READ", "USER_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/users/*").access(tenant.with("USER_WRITE"))
                        .requestMatchers(PUT, "/api/tenants/{tenantId}/users/*/roles/*").access(tenant.with("USER_WRITE"))
                        .requestMatchers(DELETE, "/api/tenants/{tenantId}/users/*/roles/*").access(tenant.with("USER_WRITE"))

                        .anyRequest().denyAll()
                );
        return httpSecurity.build();
    }
}
