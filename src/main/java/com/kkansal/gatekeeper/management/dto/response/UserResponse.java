package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String username;

    private UUID tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        if(user.getTenant() != null) {
            userResponse.setTenantId(user.getTenant().getId());
        }

        return userResponse;
    }
}
