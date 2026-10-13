package com.kkansal.gatekeeper.management.dto.request;

import com.kkansal.gatekeeper.management.entity.enums.Status;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateUpstreamRequest {

    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "slug must contain only letters and digits")
    private String slug;

    private String host;

    private Integer port;

    private Status serviceStatus;
}
