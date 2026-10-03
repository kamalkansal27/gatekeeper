package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.response.UserRoleResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserRoleService {

    private UserService userService;
    private RoleService roleService;
    private TenantService tenantService;

    @Transactional
    public UserRoleResponse addRole(UUID tenantId, String username, String roleName) {

        Tenant tenant = tenantService.findById(tenantId);

        User user = userService.findByUsernameAndTenant(tenant, username);

        Role role = roleService.findByNameAndTenant(roleName, tenant);

        Set<Role> roles = user.getRoleSet();
        roles.add(role);

        return UserRoleResponse.from(roles);
    }

    public UserRoleResponse get(UUID tenantId, String username) {

        Tenant tenant = tenantService.findById(tenantId);

        User user = userService.findByUsernameAndTenant(tenant, username);
        return UserRoleResponse.from(user.getRoleSet());
    }

    @Transactional
    public void deleteRole(UUID tenantId, String username, String roleName) {

        Tenant tenant = tenantService.findById(tenantId);

        User user = userService.findByUsernameAndTenant(tenant, username);

        Role role = roleService.findByNameAndTenant(roleName, tenant);

        Set<Role> roles = user.getRoleSet();
        roles.remove(role);
    }
}
