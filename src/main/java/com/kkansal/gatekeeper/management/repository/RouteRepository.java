package com.kkansal.gatekeeper.management.repository;

import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

    boolean existsByUpstreamAndRegisteredPathAndHttpMethod(Upstream upstream, String registeredPath, HttpMethod httpMethod);

    boolean existsByUpstreamAndId(Upstream upstream, UUID routeId);

    Optional<Route> findByUpstreamAndId(Upstream upstream, UUID routeId);

    List<Route> findAllByUpstream(Upstream upstream);
}
