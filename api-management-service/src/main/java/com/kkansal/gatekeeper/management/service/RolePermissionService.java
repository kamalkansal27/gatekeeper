package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.entity.Permission;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RolePermissionService {

    private RoleService roleService;
    private TenantService tenantService;
    private PermissionService permissionService;

    @Transactional
    public RoleResponse addPermission(UUID tenantId, String roleName, String permissionName) {

        Tenant tenant = tenantService.findById(tenantId);

        Role role = roleService.findByNameAndTenant(roleName, tenant);

        Permission permission = permissionService.findByName(permissionName);

        Set<Permission> rolePermissionSet = role.getPermissionSet();
        rolePermissionSet.add(permission);

        return RoleResponse.from(role);
    }

    @Transactional
    public RoleResponse deletePermission(UUID tenantId, String roleName, String permissionName) {

        Tenant tenant = tenantService.findById(tenantId);

        Role role = roleService.findByNameAndTenant(roleName, tenant);

        Permission permission = permissionService.findByName(permissionName);

        Set<Permission> rolePermissionSet = role.getPermissionSet();
        rolePermissionSet.remove(permission);

        return RoleResponse.from(role);
    }
}
