package com.kkansal.gatekeeper.smart_gateway_service.seeder;

import com.kkansal.gatekeeper.smart_gateway_service.client.ApiManagementClient;
import com.kkansal.gatekeeper.smart_gateway_service.data.RouteRegistry;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.logging.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SnapshotDataSeeder implements ApplicationRunner {

    private static final org.slf4j.Logger LOGGER = Logger.DATA_SEED_LOGGER;

    private ApiManagementClient apiManagementClient;
    private RouteRegistry routeRegistry;

    public SnapshotDataSeeder(ApiManagementClient apiManagementClient, RouteRegistry routeRegistry) {
        this.apiManagementClient = apiManagementClient;
        this.routeRegistry = routeRegistry;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("Snapshot data seeding started");

        SnapshotResponse snapshotResponse = apiManagementClient.getSnaphotData();
        LOGGER.info("Fetched snapshot generated at {} with {} tenants",
                snapshotResponse.generatedAt(), snapshotResponse.tenants().size());

        routeRegistry.load(snapshotResponse);
        LOGGER.info("Snapshot data seeding completed");
    }
}
