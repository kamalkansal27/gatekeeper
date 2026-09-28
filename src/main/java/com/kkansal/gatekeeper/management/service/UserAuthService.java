package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserAuthService {

    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " does not exists"));
    }

    public UserResponse create(CreateUserRequest userRequest) {

        User user = new User();
        user.setUsername(userRequest.getUsername());

        Role role = roleService.findByName("SVC_ADMIN");
        user.getRoleSet().add(role);

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return UserResponse.from(user);
    }
}
