package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.response.UserRoleResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserRoleService {

    private UserService userService;
    private RoleService roleService;

    @Transactional
    public UserRoleResponse addRole(String username, String roleName) {

        User user = userService.findByUsername(username);

        Role role = roleService.findByName(roleName);

        Set<Role> roles = user.getRoleSet();
        roles.add(role);

        return UserRoleResponse.from(roles);
    }

    public UserRoleResponse get(String username) {
        User user = userService.findByUsername(username);
        return UserRoleResponse.from(user.getRoleSet());
    }

    @Transactional
    public void deleteRole(String username, String roleName) {

        User user = userService.findByUsername(username);

        Role role = roleService.findByName(roleName);

        Set<Role> roles = user.getRoleSet();
        roles.remove(role);
    }
}
