package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RouteSnapshotResponse {

    private UUID id;

    private HttpMethod httpMethod;

    private String registeredPath;

    private String targetPath;
}
