package com.kkansal.gatekeeper.management.config;

import com.kkansal.gatekeeper.management.service.auth.CustomUserDetailsService;
import com.kkansal.gatekeeper.management.service.auth.TenantAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                                   TenantAuthorizationManager tenantAuthorizationManager) {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authenticationProvider(daoAuthenticationProvider)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("SVC_USER")
                        .requestMatchers("/api/tenants/{tenantId}/**").access(tenantAuthorizationManager)
                        .anyRequest().denyAll()
                );
        return httpSecurity.build();
    }
}
