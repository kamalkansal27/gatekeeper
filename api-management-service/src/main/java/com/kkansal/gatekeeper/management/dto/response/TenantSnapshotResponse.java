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
public class TenantSnapshotResponse {

    private UUID id;

    private Long version;

    private String slug;

    private Status status;

    @Builder.Default
    private List<UpstreamSnapshotResponse> upstreamSnapshotResponseList = new ArrayList<>();
}
