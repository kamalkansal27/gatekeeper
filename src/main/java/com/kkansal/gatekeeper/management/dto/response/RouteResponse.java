package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.Route;
import com.kkansal.gatekeeper.management.entity.enums.HttpMethod;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private String registeredPath;

    private String targetPath;

    private HttpMethod httpMethod;

    private UUID upstreamServiceId;

    private UUID tenantId;

    public static RouteResponse from(Route route) {
        return RouteResponse.builder()
                .registeredPath(route.getRegisteredPath())
                .targetPath(route.getTargetPath())
                .httpMethod(route.getHttpMethod())
                .upstreamServiceId(route.getUpstream().getId())
                .tenantId(route.getUpstream().getTenant().getId())
                .build();
    }
}
