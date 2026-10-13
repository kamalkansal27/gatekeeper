package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Permission;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PermissionResponse {

    private String permissionName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static PermissionResponse from(Permission permission) {
        return PermissionResponse.builder()
                .permissionName(permission.getName())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
