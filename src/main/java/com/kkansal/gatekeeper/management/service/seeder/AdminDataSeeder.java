package com.kkansal.gatekeeper.management.service.seeder;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.logging.Logger;
import com.kkansal.gatekeeper.management.service.RoleService;
import com.kkansal.gatekeeper.management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AdminDataSeeder implements ApplicationRunner {

    private RoleService roleService;
    private UserService userService;

    private static final org.slf4j.Logger LOGGER = Logger.DATA_SEED_LOGGER;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("Admin data seeding started");

        try {
            Role role = roleService.findByName("ROLE_ADMIN");
            LOGGER.info("Role 'ROLE_ADMIN' already exists, skipping creation");
        }
        catch (EntityNotFoundException exception) {
            roleService.create(new CreateRoleRequest("ROLE_ADMIN"));
            LOGGER.info("Created role 'ROLE_ADMIN'");
        }

        try {
            User user = userService.findByUsername("ADMIN");
            LOGGER.info("User 'Admin' already exists, skipping creation");
        }
        catch (EntityNotFoundException exception) {
            userService.create(new CreateUserRequest("ADMIN", "secret", null));
            LOGGER.info("Created user 'ADMIN'");
        }

        LOGGER.info("Admin data seeding completed");
    }
}
