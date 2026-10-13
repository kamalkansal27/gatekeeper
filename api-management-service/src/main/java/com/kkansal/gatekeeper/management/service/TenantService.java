package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.CreateTenantRequest;
import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateTenantRequest;
import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.dto.response.TenantResponse;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.entity.enums.Status;
import com.kkansal.gatekeeper.management.event.TenantCreatedEvent;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TenantService {

    private TenantRepository tenantRepository;
    private ApplicationEventPublisher eventPublisher;

    public Tenant findById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with ID: " + id));
    }

    public TenantResponse get(UUID id) {
        return TenantResponse.from(findById(id));
    }

    public List<TenantResponse> getAll() {
        return tenantRepository.findAll().stream()
                .map(TenantResponse::from)
                .toList();
    }

    @Transactional
    public TenantResponse create(CreateTenantRequest tenantRequest) {

        if(tenantRepository.findBySlug(tenantRequest.getSlug()) != null) {
            throw new DuplicateResourceException("Tenant with slug name '" + tenantRequest.getSlug() + "' already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(tenantRequest.getName())
                .slug(tenantRequest.getSlug())
                .tenantStatus(Status.ACTIVE)
                .build();

        tenantRepository.save(tenant);
        eventPublisher.publishEvent(new TenantCreatedEvent(tenant));

        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse update(UUID id, UpdateTenantRequest tenantRequest) {

        if(tenantRequest.getSlug() != null && tenantRepository.findBySlug(tenantRequest.getSlug()) != null) {
            throw new DuplicateResourceException("Tenant with slug name '" + tenantRequest.getSlug() + "' already exists");
        }

        Tenant tenant = findById(id);
        tenant.setName(tenantRequest.getName() != null ? tenantRequest.getName() : tenant.getName());
        tenant.setSlug(tenantRequest.getSlug() != null ? tenantRequest.getSlug() : tenant.getSlug());
        tenant.setTenantStatus(tenantRequest.getTenantStatus() != null ? tenantRequest.getTenantStatus() : tenant.getTenantStatus());
        return TenantResponse.from(tenant);
    }

    @Transactional
    public void delete(UUID id) {
       tenantRepository.delete(findById(id));
    }
}
