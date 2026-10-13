package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.PermissionRequest;
import com.kkansal.gatekeeper.management.dto.response.PermissionResponse;
import com.kkansal.gatekeeper.management.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/permissions")
public class PermissionController {

    private PermissionService permissionService;

    @PostMapping
    public PermissionResponse create(@RequestBody @Valid PermissionRequest permissionRequest) {
        return permissionService.create(permissionRequest);
    }

    @GetMapping
    public List<PermissionResponse> getAll() {
        return permissionService.get();
    }

    @PatchMapping("/{name}")
    public PermissionResponse update(@PathVariable String name, @RequestBody @Valid PermissionRequest permissionRequest) {
        return permissionService.update(name, permissionRequest);
    }

    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        permissionService.delete(name);
    }
}
