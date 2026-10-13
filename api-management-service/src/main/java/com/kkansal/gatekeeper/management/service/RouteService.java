package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateRouteRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateRouteRequest;
import com.kkansal.gatekeeper.management.dto.response.RouteResponse;
import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.RouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RouteService {

    private RouteRepository routeRepository;
    private UpstreamService upstreamService;

    public Route findByUpstreamAndId(Upstream upstream, UUID routeId) {
        return routeRepository.findByUpstreamAndId(upstream, routeId)
                .orElseThrow(() -> new EntityNotFoundException("No Route Found with ID : " + routeId));
    }

    public List<RouteResponse> getAllByUpstream(UUID tenantId, UUID upstreamId) {

        Upstream upstream = upstreamService.findByIdAndTenant(tenantId, upstreamId);

        return routeRepository.findAllByUpstream(upstream).stream()
                .map(RouteResponse::from)
                .toList();
    }

    public RouteResponse get(UUID tenantId, UUID upstreamId, UUID routeId) {

        Upstream upstream = upstreamService.findByIdAndTenant(tenantId, upstreamId);

        return RouteResponse.from(findByUpstreamAndId(upstream, routeId));

    }

    @Transactional
    public RouteResponse create(UUID tenantId, UUID upstreamId, CreateRouteRequest routeRequest) {

        Upstream upstream = upstreamService.findByIdAndTenant(tenantId, upstreamId);

        if (routeRepository.existsByUpstreamAndRegisteredPathAndHttpMethod(
                upstream, routeRequest.getRegisteredPath(), routeRequest.getHttpMethod())) {
            throw new DuplicateResourceException(
                    "Route '" + routeRequest.getHttpMethod() + " " + routeRequest.getRegisteredPath()
                            + "' already exists in Upstream - " + upstream.getName()
            );
        }

        Route route = Route.builder()
                .registeredPath(routeRequest.getRegisteredPath())
                .targetPath(routeRequest.getTargetPath())
                .httpMethod(routeRequest.getHttpMethod())
                .upstream(upstream)
                .build();

        routeRepository.save(route);

        return RouteResponse.from(route);
    }

    @Transactional
    public RouteResponse update(UUID tenantId, UUID upstreamId, UUID routeId, UpdateRouteRequest routeRequest) {

        Upstream upstream = upstreamService.findByIdAndTenant(tenantId, upstreamId);

        Route route = findByUpstreamAndId(upstream, routeId);

        route.setHttpMethod((routeRequest.getHttpMethod() != null) ? routeRequest.getHttpMethod() : route.getHttpMethod());
        route.setRegisteredPath((routeRequest.getRegisteredPath() != null) ? routeRequest.getRegisteredPath() : route.getRegisteredPath());
        route.setTargetPath((routeRequest.getTargetPath() != null) ? routeRequest.getTargetPath() : route.getTargetPath());

        return RouteResponse.from(route);
    }

    @Transactional
    public void delete(UUID tenantId, UUID upstreamId, UUID routeId) {

        Upstream upstream = upstreamService.findByIdAndTenant(tenantId, upstreamId);

        Route route = findByUpstreamAndId(upstream, routeId);

        routeRepository.delete(route);
    }
}
