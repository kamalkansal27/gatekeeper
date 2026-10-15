package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.response.SnapshotResponse;
import com.kkansal.gatekeeper.management.service.SnapshotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotController {

    private SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @GetMapping("/internal/routes/snapshot")
    public SnapshotResponse getRouteSnaphot() {
       return snapshotService.get();
    }
}
