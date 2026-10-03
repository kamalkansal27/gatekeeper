package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.service.RolePermissionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tenants/{tenantId}/roles/{roleName}/permissions")
public class RolePermissionController {

    private RolePermissionService rolePermissionService;

    @PutMapping("/{permissionName}")
    public RoleResponse addPermission(@PathVariable UUID tenantId, @PathVariable String roleName, @PathVariable String permissionName) {
        return rolePermissionService.addPermission(tenantId, roleName, permissionName);
    }

    @DeleteMapping("/{permissionName}")
    public RoleResponse deletePermission(@PathVariable UUID tenantId, @PathVariable String roleName, @PathVariable String permissionName) {
        return rolePermissionService.deletePermission(tenantId, roleName, permissionName);
    }
}

