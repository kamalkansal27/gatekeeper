package com.kkansal.gatekeeper.management.aspect;

import com.kkansal.gatekeeper.management.dto.response.RouteResponse;
import com.kkansal.gatekeeper.management.dto.response.TenantResponse;
import com.kkansal.gatekeeper.management.dto.response.UpstreamResponse;
import com.kkansal.gatekeeper.management.entity.OutboxEvent;
import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.event.ApiDataChangeEvent;
import com.kkansal.gatekeeper.management.repository.OutboxEventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@AllArgsConstructor
public class ApiDataChangeOutboxAspect {

    public static final String TOPIC = "api-data-change";

    private static final String SERVICE = "com.kkansal.gatekeeper.management.service.";

    private final OutboxEventRepository outboxEventRepository;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    @AfterReturning(
            pointcut = "execution(* " + SERVICE + "TenantService.create(..)) || execution(* " + SERVICE + "TenantService.update(..))",
            returning = "tenant"
    )
    public void recordTenantUpsert(TenantResponse tenant) {
        saveToOutbox(ApiDataChangeEvent.upsert(loadFlushed(Tenant.class, tenant.getId())));
    }

    @AfterReturning(
            pointcut = "execution(* " + SERVICE + "UpstreamService.create(..)) || execution(* " + SERVICE + "UpstreamService.update(..))",
            returning = "upstream"
    )
    public void recordUpstreamUpsert(UpstreamResponse upstream) {
        saveToOutbox(ApiDataChangeEvent.upsert(loadFlushed(Upstream.class, upstream.getId())));
    }

    @AfterReturning(
            pointcut = "execution(* " + SERVICE + "RouteService.create(..)) || execution(* " + SERVICE + "RouteService.update(..))",
            returning = "route"
    )
    public void recordRouteUpsert(RouteResponse route) {
        saveToOutbox(ApiDataChangeEvent.upsert(loadFlushed(Route.class, route.getId())));
    }

    @AfterReturning(
            pointcut = "execution(java.util.List " + SERVICE + "RouteService.createAll(..))",
            returning = "routes"
    )
    public void recordBulkRouteCreation(List<RouteResponse> routes) {
        routes.forEach(this::recordRouteUpsert);
    }

    // Deletes read the entity before the service removes it, since nothing is left to load afterwards.
    @Around("execution(void " + SERVICE + "TenantService.delete(..)) && args(tenantId)")
    public void recordTenantDelete(ProceedingJoinPoint joinPoint, UUID tenantId) throws Throwable {
        Tenant tenant = entityManager.find(Tenant.class, tenantId);
        joinPoint.proceed();
        saveToOutbox(ApiDataChangeEvent.delete(tenant));
    }

    @Around("execution(void " + SERVICE + "UpstreamService.delete(..)) && args(*, upstreamId)")
    public void recordUpstreamDelete(ProceedingJoinPoint joinPoint, UUID upstreamId) throws Throwable {
        Upstream upstream = entityManager.find(Upstream.class, upstreamId);
        joinPoint.proceed();
        saveToOutbox(ApiDataChangeEvent.delete(upstream));
    }

    @Around("execution(void " + SERVICE + "RouteService.delete(..)) && args(*, *, routeId)")
    public void recordRouteDelete(ProceedingJoinPoint joinPoint, UUID routeId) throws Throwable {
        Route route = entityManager.find(Route.class, routeId);
        joinPoint.proceed();
        saveToOutbox(ApiDataChangeEvent.delete(route));
    }

    // Hibernate bumps @Version only when it flushes, so flush first or the event carries the old version.
    private <T> T loadFlushed(Class<T> type, UUID id) {
        entityManager.flush();
        T entity = entityManager.find(type, id);
        if (entity == null) {
            throw new EntityNotFoundException("No " + type.getSimpleName() + " Found with ID : " + id);
        }
        return entity;
    }

    private void saveToOutbox(ApiDataChangeEvent event) {

        // Without an enclosing transaction the save would commit on its own, silently losing atomicity.
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("Outbox write to " + TOPIC + " must run inside the business transaction");
        }

        outboxEventRepository.save(OutboxEvent.builder()
                .topic(TOPIC)
                .messageKey(event.tenantId().toString())
                .payload(objectMapper.writeValueAsString(event))
                .build());
    }
}
