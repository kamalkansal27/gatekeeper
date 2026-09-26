package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateUpstreamRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateUpstreamRequest;
import com.kkansal.gatekeeper.management.dto.response.UpstreamResponse;
import com.kkansal.gatekeeper.management.entity.Upstream;
import com.kkansal.gatekeeper.management.service.UpstreamService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UpstreamController {

    private UpstreamService upstreamService;

    @GetMapping("/upstreams")
    public List<UpstreamResponse> getAll(){
        return upstreamService.getAll();
    }

    @GetMapping("/upstreams/{upstreamId}")
    public UpstreamResponse get(@PathVariable UUID upstreamId) {
        return upstreamService.get(upstreamId);
    }

    @GetMapping("/tenants/{tenantId}/upstreams")
    public List<UpstreamResponse> getAllByTenant(@PathVariable UUID tenantId) {
        return upstreamService.getAllByTenantId(tenantId);
    }

    @PostMapping("/tenants/{tenantId}/upstreams")
    public UpstreamResponse create(@PathVariable UUID tenantId, @RequestBody @Valid CreateUpstreamRequest upstreamRequest) {
        return upstreamService.create(tenantId, upstreamRequest);
    }

    @PatchMapping("/tenants/{tenantId}/upstreams/{upstreamId}")
    public UpstreamResponse update(@PathVariable UUID tenantId, @PathVariable UUID upstreamId, @RequestBody @Valid UpdateUpstreamRequest upstreamRequest) {
        return upstreamService.update(tenantId, upstreamId, upstreamRequest);
    }

    @DeleteMapping("/upstreams/{upstreamId}")
    public void delete(@PathVariable UUID upstreamId) {
        upstreamService.delete(upstreamId);
    }
}
