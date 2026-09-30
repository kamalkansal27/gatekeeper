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
@RequestMapping("/api/admin/roles")
@AllArgsConstructor
public class RoleController {

    private RoleService roleService;

    @PostMapping
    public RoleResponse create(@RequestBody @Valid CreateRoleRequest roleRequest) {
        return roleService.create(roleRequest);
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public RoleResponse get(@PathVariable UUID id) {
        return roleService.get(id);
    }

    @PatchMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateRoleRequest roleRequest) {
        return roleService.update(id, roleRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
