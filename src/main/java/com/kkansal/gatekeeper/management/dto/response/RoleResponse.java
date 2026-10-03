package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Permission;
import com.kkansal.gatekeeper.management.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private UUID id;

    private String name;

    private UUID tenantId;

    private Set<Permission> permissionSet;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static RoleResponse from(Role role) {
        RoleResponse roleResponse = RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissionSet(role.getPermissionSet())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();

        if(role.getTenant() != null) {
            roleResponse.setTenantId(role.getTenant().getId());
        }

        return roleResponse;
    }
}
