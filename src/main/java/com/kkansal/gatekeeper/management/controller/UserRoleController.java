package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.response.UserRoleResponse;
import com.kkansal.gatekeeper.management.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/users/{username}/roles")
public class UserRoleController {

    private UserRoleService userRoleService;

    @GetMapping
    public UserRoleResponse get(@PathVariable String username) {
        return userRoleService.get(username);
    }

    @PutMapping("/{roleName}")
    public UserRoleResponse addRole(@PathVariable String username, @PathVariable String roleName) {
        return userRoleService.addRole(username, roleName);
    }

    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable String username, @PathVariable String roleName) {
        userRoleService.deleteRole(username, roleName);
    }
}
