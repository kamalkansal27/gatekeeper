package com.kkansal.gatekeeper.management.aspect;

import com.kkansal.gatekeeper.management.dto.response.RouteEntry;
import com.kkansal.gatekeeper.management.dto.response.RouteResponse;
import com.kkansal.gatekeeper.management.entity.OutboxEvent;
import com.kkansal.gatekeeper.management.repository.OutboxEventRepository;
import com.kkansal.gatekeeper.management.repository.RouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@AllArgsConstructor
public class RouteOutboxAspect {

    public static final String SINGLE_ROUTE_TOPIC = "single-route-update";
    public static final String BULK_ROUTE_TOPIC = "bulk-route-updates";

    private final OutboxEventRepository outboxEventRepository;
    private final RouteRepository routeRepository;
    private final ObjectMapper objectMapper;

    @AfterReturning(
            pointcut = "execution(com.kkansal.gatekeeper.management.dto.response.RouteResponse com.kkansal.gatekeeper.management.service.RouteService.create(..))",
            returning = "route"
    )
    public void recordRouteCreation(RouteResponse route) {
        saveToOutbox(SINGLE_ROUTE_TOPIC, toRouteEntry(route));
    }

    @AfterReturning(
            pointcut = "execution(java.util.List com.kkansal.gatekeeper.management.service.RouteService.createAll(..))",
            returning = "routes"
    )
    public void recordBulkRouteCreation(List<RouteResponse> routes) {
        saveToOutbox(BULK_ROUTE_TOPIC, routes.stream().map(this::toRouteEntry).toList());
    }

    private RouteEntry toRouteEntry(RouteResponse route) {
        return routeRepository.findById(route.getId())
                .map(RouteEntry::from)
                .orElseThrow(() -> new EntityNotFoundException("No Route Found with ID : " + route.getId()));
    }

    private void saveToOutbox(String topic, Object payload) {

        // Without an enclosing transaction the save would commit on its own, silently losing atomicity.
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("Outbox write to " + topic + " must run inside the business transaction");
        }

        outboxEventRepository.save(OutboxEvent.builder()
                .topic(topic)
                .payload(objectMapper.writeValueAsString(payload))
                .build());
    }
}
