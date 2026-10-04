package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateRoleRequest;
import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleService {

    private RoleRepository roleRepository;
    private TenantService tenantService;

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name : " + roleName));
    }

    public Role findByNameAndTenant(String name, Tenant tenant) {
        return roleRepository.findByNameAndTenant(name, tenant)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name : " + name));
    }

    public Role findByIdAndTenant(UUID id, Tenant tenant){
        return roleRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID : " + id));
    }

    public RoleResponse get(UUID tenantId, UUID roleId) {

        Tenant tenant = tenantService.findById(tenantId);
        return RoleResponse.from(findByIdAndTenant(roleId, tenant));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest roleRequest) {

        Role role = Role.builder()
                .name(roleRequest.getName())
                .build();

        roleRepository.save(role);
        return RoleResponse.from(role);
    }

    @Transactional
    public RoleResponse createWithTenantId(UUID tenantId, CreateRoleRequest roleRequest) {

        Tenant tenant = tenantService.findById(tenantId);

        if(!roleRepository.findByNameAndTenant(roleRequest.getName(), tenant).isEmpty()) {
            throw new DuplicateResourceException("Role with name '" + roleRequest.getName()
                    + "' already exists in tenant " + tenant.getName());
        }

        Role role = Role.builder()
                .name(roleRequest.getName())
                .tenant(tenant)
                .build();

        roleRepository.save(role);
        return RoleResponse.from(role);
    }

    @Transactional
    public RoleResponse update(UUID tenantId, UUID roleId, UpdateRoleRequest roleRequest) {

        Tenant tenant = tenantService.findById(tenantId);

        Role role = findByIdAndTenant(roleId, tenant);
        role.setName(roleRequest.getName() != null ? roleRequest.getName() : role.getName());

        return RoleResponse.from(role);
    }

    @Transactional
    public void delete(UUID tenantId, UUID roleId) {

        Tenant tenant = tenantService.findById(tenantId);

        Role role = findByIdAndTenant(roleId, tenant);
        roleRepository.delete(role);
    }
}
