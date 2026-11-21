package com.kkansal.gatekeeper.smart_gateway_service.consumer;

import com.kkansal.gatekeeper.smart_gateway_service.data.RouteRegistry;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.ApiDataChangeEvent;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.RoutePayload;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.TenantPayload;
import com.kkansal.gatekeeper.smart_gateway_service.dto.event.UpstreamPayload;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.kkansal.gatekeeper.smart_gateway_service.dto.event.ApiDataChangeEvent.Operation.UPSERT;

/**
 * Started by the snapshot seeder before it fetches the snapshot, so no change can fall between the two.
 * Until {@link #goLive()} events are only buffered; they are then replayed on top of the snapshot,
 * and the registry's version checks drop the ones the snapshot already contains.
 *
 * Every gateway instance needs every event, so each one joins its own consumer group.
 */
@Component
public class ApiDataChangeListener implements ConsumerSeekAware {

    public static final String LISTENER_ID = "api-data-change";
    private static final String TOPIC = "api-data-change";

    private final RouteRegistry routeRegistry;
    private final ObjectMapper objectMapper;

    private final CountDownLatch partitionsAssigned = new CountDownLatch(1);
    private final List<ApiDataChangeEvent> pending = new ArrayList<>();
    private boolean live;

    public ApiDataChangeListener(RouteRegistry routeRegistry, ObjectMapper objectMapper) {
        this.routeRegistry = routeRegistry;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(id = LISTENER_ID, topics = TOPIC, groupId = "smart-gateway-${random.uuid}", autoStartup = "false")
    public synchronized void onMessage(String message) {

        ApiDataChangeEvent event = objectMapper.readValue(message, ApiDataChangeEvent.class);
        if (live) {
            apply(event);
        } else {
            pending.add(event);
        }
    }

    // The container resolves each partition's starting offset before this callback runs,
    // so from here on every newly published event is guaranteed to be consumed.
    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        partitionsAssigned.countDown();
    }

    public void awaitPartitionAssignment(Duration timeout) throws InterruptedException {

        if (!partitionsAssigned.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            throw new IllegalStateException("No partitions of " + TOPIC + " assigned within " + timeout);
        }
    }

    public synchronized void goLive() {

        pending.forEach(this::apply);
        pending.clear();
        live = true;
    }

    private void apply(ApiDataChangeEvent event) {

        boolean upsert = event.operation() == UPSERT;

        switch (event.entityType()) {
            case TENANT -> {
                if (upsert) {
                    routeRegistry.upsertTenant(event.tenantId(), event.version(), payload(event, TenantPayload.class));
                } else {
                    routeRegistry.deleteTenant(event.tenantId(), event.version());
                }
            }
            case UPSTREAM -> {
                if (upsert) {
                    routeRegistry.upsertUpstream(event.tenantId(), event.upstreamId(), event.version(), payload(event, UpstreamPayload.class));
                } else {
                    routeRegistry.deleteUpstream(event.tenantId(), event.upstreamId(), event.version());
                }
            }
            case ROUTE -> {
                if (upsert) {
                    routeRegistry.upsertRoute(event.tenantId(), event.upstreamId(), event.routeId(), event.version(), payload(event, RoutePayload.class));
                } else {
                    routeRegistry.deleteRoute(event.tenantId(), event.upstreamId(), event.routeId(), event.version());
                }
            }
        }
    }

    private <T> T payload(ApiDataChangeEvent event, Class<T> type) {
        return objectMapper.treeToValue(event.payload(), type);
    }
}
