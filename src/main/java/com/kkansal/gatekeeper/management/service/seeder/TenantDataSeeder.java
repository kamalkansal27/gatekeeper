package com.kkansal.gatekeeper.management.service.seeder;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.dto.response.UserRoleResponse;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.enums.AppPermission;
import com.kkansal.gatekeeper.management.event.TenantCreatedEvent;
import com.kkansal.gatekeeper.management.logging.Logger;
import com.kkansal.gatekeeper.management.service.RolePermissionService;
import com.kkansal.gatekeeper.management.service.RoleService;
import com.kkansal.gatekeeper.management.service.UserRoleService;
import com.kkansal.gatekeeper.management.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TenantDataSeeder {

    private UserService userService;
    private RoleService roleService;
    private UserRoleService userRoleService;
    private RolePermissionService rolePermissionService;
    private Logger logger;

    private static final String ROLE_NAME = "ROLE_SUPER_TENANT_USER";
    private static final String USERNAME = "Super Tenant User";
    private static final org.slf4j.Logger LOGGER = Logger.DATA_SEED_LOGGER;

    @EventListener
    public void onTenantCreated(TenantCreatedEvent event) {
        createInitialTenantData(event.tenant());
    }

    public void createInitialTenantData(Tenant tenant) {

        LOGGER.info("Tenant data seeding started for tenant '{}' ({})", tenant.getSlug(), tenant.getId());

        userService.createWithTenantId(tenant.getId(),
                new CreateUserRequest(USERNAME, "secret", tenant.getId()));
        LOGGER.info("Created initial user '{}' for tenant {}", USERNAME, tenant.getId());

        roleService.createWithTenantId(tenant.getId(),
                new CreateRoleRequest(ROLE_NAME));
        LOGGER.info("Created role '{}' for tenant {}", ROLE_NAME, tenant.getId());

        for (AppPermission permission : AppPermission.values()) {
            rolePermissionService.addPermission(tenant.getId(), ROLE_NAME, permission.name());
            LOGGER.debug("Granted permission '{}' to role '{}' for tenant {}", permission, ROLE_NAME, tenant.getId());
        }
        LOGGER.info("Granted all {} permissions to role '{}' for tenant {}",
                AppPermission.values().length, ROLE_NAME, tenant.getId());

        userRoleService.addRole(tenant.getId(), USERNAME, ROLE_NAME);
        LOGGER.info("Assigned role '{}' to user '{}' for tenant {}", ROLE_NAME, USERNAME, tenant.getId());

        LOGGER.info("Tenant data seeding completed for tenant '{}' ({})", tenant.getSlug(), tenant.getId());

    }
}
