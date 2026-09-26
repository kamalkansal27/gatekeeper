package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateTenantRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateTenantRequest;
import com.kkansal.gatekeeper.management.dto.response.TenantResponse;
import com.kkansal.gatekeeper.management.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public List<TenantResponse> getAll() {
        return tenantService.getAll();
    }

    @GetMapping("/{id}")
    public TenantResponse get(@PathVariable UUID id) {
        return tenantService.get(id);
    }

    @PostMapping
    public TenantResponse create(@RequestBody @Valid CreateTenantRequest tenantRequest) {
        return tenantService.create(tenantRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        tenantService.delete(id);
    }

    @PatchMapping("/{id}")
    public TenantResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateTenantRequest tenantRequest) {
        return tenantService.update(id, tenantRequest);
    }
}
