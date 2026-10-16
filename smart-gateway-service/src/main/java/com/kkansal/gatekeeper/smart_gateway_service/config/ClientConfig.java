package com.kkansal.gatekeeper.smart_gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    RestClient managementRestClient(RestClient.Builder builder,
                                    @Value("${api-management.base-url}") String baseUrl,
                                    @Value("${api-management.username}") String username,
                                    @Value("${api-management.password}") String password) {

        return builder
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth(username, password))
                .build();
    }
}
