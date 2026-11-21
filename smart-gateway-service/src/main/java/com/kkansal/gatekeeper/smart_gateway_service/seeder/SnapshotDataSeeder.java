package com.kkansal.gatekeeper.smart_gateway_service.seeder;

import com.kkansal.gatekeeper.smart_gateway_service.client.ApiManagementClient;
import com.kkansal.gatekeeper.smart_gateway_service.consumer.ApiDataChangeListener;
import com.kkansal.gatekeeper.smart_gateway_service.data.RouteRegistry;
import com.kkansal.gatekeeper.smart_gateway_service.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.smart_gateway_service.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SnapshotDataSeeder implements ApplicationRunner {

    private static final org.slf4j.Logger LOGGER = Logger.DATA_SEED_LOGGER;

    private final ApiManagementClient apiManagementClient;
    private final RouteRegistry routeRegistry;
    private final ApiDataChangeListener apiDataChangeListener;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final Duration partitionAssignmentTimeout;

    public SnapshotDataSeeder(ApiManagementClient apiManagementClient,
                              RouteRegistry routeRegistry,
                              ApiDataChangeListener apiDataChangeListener,
                              KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                              @Value("${gateway.kafka.partition-assignment-timeout:30s}") Duration partitionAssignmentTimeout) {
        this.apiManagementClient = apiManagementClient;
        this.routeRegistry = routeRegistry;
        this.apiDataChangeListener = apiDataChangeListener;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.partitionAssignmentTimeout = partitionAssignmentTimeout;
    }

    /** The listener must be consuming before the snapshot is taken; see {@link ApiDataChangeListener}. */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("Snapshot data seeding started");

        kafkaListenerEndpointRegistry.getListenerContainer(ApiDataChangeListener.LISTENER_ID).start();
        apiDataChangeListener.awaitPartitionAssignment(partitionAssignmentTimeout);
        LOGGER.info("Data change listener assigned; buffering events until the snapshot is loaded");

        SnapshotResponse snapshotResponse = apiManagementClient.getSnaphotData();
        LOGGER.info("Fetched snapshot generated at {} with {} tenants",
                snapshotResponse.generatedAt(), snapshotResponse.tenants().size());

        routeRegistry.load(snapshotResponse);
        apiDataChangeListener.goLive();
        LOGGER.info("Snapshot data seeding completed; applying data changes live");
    }
}
