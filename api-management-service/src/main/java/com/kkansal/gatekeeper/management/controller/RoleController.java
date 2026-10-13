package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateRoleRequest;
import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.service.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RoleController {

    private RoleService roleService;

    @PostMapping("/admin/roles")
    public RoleResponse create(@RequestBody @Valid CreateRoleRequest roleRequest) {
        return roleService.create(roleRequest);
    }

    @PostMapping("/tenants/{tenantId}/roles")
    public RoleResponse createWithTenantId(@PathVariable UUID tenantId, @RequestBody @Valid CreateRoleRequest roleRequest) {
        return roleService.createWithTenantId(tenantId, roleRequest);
    }

    @GetMapping("/admin/roles")
    public List<RoleResponse> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/tenants/{tenantId}/roles/{roleId}")
    public RoleResponse get(@PathVariable UUID tenantId, @PathVariable UUID roleId) {
        return roleService.get(tenantId, roleId);
    }

    @PatchMapping("/tenants/{tenantId}/roles/{roleId}")
    public RoleResponse update(@PathVariable UUID tenantId, @PathVariable UUID roleId, @RequestBody @Valid UpdateRoleRequest roleRequest) {
        return roleService.update(tenantId, roleId, roleRequest);
    }

    @DeleteMapping("/tenants/{tenantId}/roles/{roleId}")
    public void delete(@PathVariable UUID tenantId, @PathVariable UUID roleId) {
        roleService.delete(tenantId, roleId);
    }
}
