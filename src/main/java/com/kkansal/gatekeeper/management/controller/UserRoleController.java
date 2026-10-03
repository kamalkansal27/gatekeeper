package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.response.UserRoleResponse;
import com.kkansal.gatekeeper.management.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tenants/{tenantId}/users/{username}/roles")
public class UserRoleController {

    private UserRoleService userRoleService;

    @GetMapping
    public UserRoleResponse get(@PathVariable UUID tenantId, @PathVariable String username) {
        return userRoleService.get(tenantId, username);
    }

    @PutMapping("/{roleName}")
    public UserRoleResponse addRole(@PathVariable UUID tenantId, @PathVariable String username, @PathVariable String roleName) {
        return userRoleService.addRole(tenantId, username, roleName);
    }

    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable UUID tenantId, @PathVariable String username, @PathVariable String roleName) {
        userRoleService.deleteRole(tenantId, username, roleName);
    }
}
