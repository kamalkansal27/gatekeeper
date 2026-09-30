package com.kkansal.gatekeeper.management.repository;

import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.Upstream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpstreamRepository extends JpaRepository<Upstream, UUID> {

    Upstream findBySlugAndTenant(String slugName, Tenant tenant);

    List<Upstream> findAllByTenant(Tenant tenant);

    Optional<Upstream> findByIdAndTenantId(UUID id, UUID tenantId);
}
