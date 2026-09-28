package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private UUID id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static RoleResponse from(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
