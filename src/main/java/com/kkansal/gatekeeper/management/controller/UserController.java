package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @GetMapping("/admin/users")
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @PostMapping("/admin/users/register")
    public UserResponse create(@RequestBody @Valid CreateUserRequest userRequest) {
       return userService.create(userRequest);
    }

    @PostMapping("/tenants/{tenantId}/users/register")
    public UserResponse createWithTenantId(@PathVariable UUID tenantId, @RequestBody @Valid CreateUserRequest createUserRequest){
        return userService.createWithTenantId(tenantId, createUserRequest);
    }

    @GetMapping("/tenants/{tenantId}/users/{username}")
    public UserResponse get(@PathVariable UUID tenantId, @PathVariable String username) {
        return userService.get(tenantId, username);
    }

    @DeleteMapping("/tenants/{tenantId}/users/{username}")
    public void delete(@PathVariable UUID tenantId, @PathVariable String username) {
        userService.delete(tenantId, username);
    }
}
