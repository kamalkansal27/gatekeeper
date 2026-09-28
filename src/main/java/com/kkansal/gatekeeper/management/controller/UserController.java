package com.kkansal.gatekeeper.management.controller;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.service.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserAuthService userAuthService;

    @PostMapping("/register")
    public UserResponse create(@RequestBody CreateUserRequest userRequest) {
       return userAuthService.create(userRequest);
    }
}
