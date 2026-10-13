package com.kkansal.gatekeeper.management.service.seeder;

import com.kkansal.gatekeeper.management.dto.request.PermissionRequest;
import com.kkansal.gatekeeper.management.dto.response.PermissionResponse;
import com.kkansal.gatekeeper.management.entity.enums.AppPermission;
import com.kkansal.gatekeeper.management.logging.Logger;
import com.kkansal.gatekeeper.management.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class PermissionSeeder implements ApplicationRunner {

    private PermissionService permissionService;

    private static final org.slf4j.Logger LOGGER = Logger.DATA_SEED_LOGGER;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("Permission seeding started");

        List<String> existingPermissions = permissionService.get().stream()
                .map(PermissionResponse::getPermissionName)
                .toList();

        List<String> newPermissions = Arrays.stream(AppPermission.values())
                .map(Enum::name)
                .filter(name -> !existingPermissions.contains(name))
                .toList();

        for (String permissionName : newPermissions) {
            permissionService.create(new PermissionRequest(permissionName));
            LOGGER.debug("Created permission '{}'", permissionName);
        }

        LOGGER.info("Permission seeding completed: {} existing, {} newly inserted {}",
                existingPermissions.size(), newPermissions.size(), newPermissions);
    }
}
