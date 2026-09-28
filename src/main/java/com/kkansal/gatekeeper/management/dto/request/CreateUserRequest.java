package com.kkansal.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
