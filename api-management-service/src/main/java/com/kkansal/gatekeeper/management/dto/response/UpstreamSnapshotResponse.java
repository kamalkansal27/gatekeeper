package com.kkansal.gatekeeper.management.dto.response;

import com.kkansal.gatekeeper.management.entity.enums.Status;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpstreamSnapshotResponse {

    private UUID id;

    private String slug;

    private Status status;

    private String host;

    private Integer port;

    @Builder.Default
    private List<RouteSnapshotResponse> routeSnapshotResponseList = new ArrayList<>();
}
