package com.kkansal.gatekeeper.management.dto.request;

import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateRouteRequest {

    private String registeredPath;

    private String targetPath;

    private HttpMethod httpMethod;
}
