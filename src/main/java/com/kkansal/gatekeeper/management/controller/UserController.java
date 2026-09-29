package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
       return userService.create(userRequest);
    }

    @GetMapping("/{username}")
    public UserResponse get(@PathVariable String username) {
        return userService.get(username);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username) {
        userService.delete(username);
    }
}
