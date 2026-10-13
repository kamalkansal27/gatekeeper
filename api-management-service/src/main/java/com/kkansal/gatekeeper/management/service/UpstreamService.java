package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateUpstreamRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateUpstreamRequest;
import com.kkansal.gatekeeper.management.dto.response.UpstreamResponse;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.entity.enums.Status;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.UpstreamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UpstreamService {

    private UpstreamRepository upstreamRepository;
    private TenantService tenantService;

    public UpstreamService(UpstreamRepository upstreamRepository, TenantService tenantService) {
        this.upstreamRepository = upstreamRepository;
        this.tenantService = tenantService;
    }

    public Upstream findByIdAndTenant(UUID tenantId, UUID upstreamId) {
        return upstreamRepository.findByIdAndTenantId(upstreamId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Upstream not found with ID: " + upstreamId));
    }

    public UpstreamResponse get(UUID tenantId, UUID upstreamId) {
        return UpstreamResponse.from(findByIdAndTenant(tenantId, upstreamId));
    }

    public List<UpstreamResponse> getAll() {
        return upstreamRepository.findAll().stream()
                .map(UpstreamResponse::from)
                .toList();
    }

    public List<UpstreamResponse> getAllByTenantId(UUID tenantId) {
        Tenant tenant = tenantService.findById(tenantId);
        return upstreamRepository.findAllByTenant(tenant).stream()
                .map(UpstreamResponse::from)
                .toList();
    }

    @Transactional
    public UpstreamResponse create(UUID tenantId, CreateUpstreamRequest upstreamRequest) {

        Tenant tenant = tenantService.findById(tenantId);

        if(upstreamRepository.findBySlugAndTenant(upstreamRequest.getSlug(), tenant) != null) {
            throw new DuplicateResourceException(
                    "Upstream with slug name '" + upstreamRequest.getSlug() + "' already exists in Tenant - " + tenant.getName()
            );
        }

        Upstream upstream = Upstream.builder()
                .name(upstreamRequest.getName())
                .slug(upstreamRequest.getSlug())
                .serviceStatus(Status.ACTIVE)
                .host(upstreamRequest.getHost())
                .port(upstreamRequest.getPort())
                .tenant(tenant)
                .build();

        upstreamRepository.save(upstream);
        return UpstreamResponse.from(upstream);
    }

    @Transactional
    public UpstreamResponse update(UUID tenantId, UUID upstreamId, UpdateUpstreamRequest upstreamRequest) {

        Upstream upstream = findByIdAndTenant(tenantId, upstreamId);
        Tenant tenant = upstream.getTenant();

        if(upstreamRequest.getSlug() != null) {
            Upstream existing = upstreamRepository.findBySlugAndTenant(upstreamRequest.getSlug(), tenant);
            if(existing != null && !existing.getId().equals(upstreamId)) {
                throw new DuplicateResourceException(
                        "Upstream with slug name '" + upstreamRequest.getSlug() + "' already exists in Tenant - " + tenant.getName()
                );
            }
        }

        upstream.setName(upstreamRequest.getName() == null ? upstream.getName() : upstreamRequest.getName());
        upstream.setSlug(upstreamRequest.getSlug() == null ? upstream.getSlug() : upstreamRequest.getSlug());
        upstream.setHost(upstreamRequest.getHost() == null ? upstream.getHost() : upstreamRequest.getHost());
        upstream.setPort(upstreamRequest.getPort() == null ? upstream.getPort() : upstreamRequest.getPort());
        upstream.setServiceStatus(upstreamRequest.getServiceStatus() == null ? upstream.getServiceStatus() : upstreamRequest.getServiceStatus());

        return UpstreamResponse.from(upstream);
    }

    @Transactional
    public void delete(UUID tenantId, UUID upstreamId) {
        upstreamRepository.delete(findByIdAndTenant(tenantId, upstreamId));
    }
}
