package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Role;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleResponse {

    private Set<String> roles;

    public static UserRoleResponse from(Set<Role> roleSet) {
        Set<String> roles = roleSet.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserRoleResponse.builder()
                .roles(roles)
                .build();
    }
}
