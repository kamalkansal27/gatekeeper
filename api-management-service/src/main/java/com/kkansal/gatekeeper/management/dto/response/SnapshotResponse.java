package com.kkansal.gatekeeper.management.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotResponse {

    private LocalDateTime generatedAt;

    @Builder.Default
    private List<TenantSnapshotResponse> tenantSnapshotResponseList = new ArrayList<>();
}
