package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleService roleService;
    private TenantService tenantService;
    private PasswordEncoder passwordEncoder;

    public User findByUsernameAndTenantId(UUID tenantId, String username) {
        return userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse get(UUID tenantId, String username) {
        User user = findByUsernameAndTenantId(tenantId, username);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse create(CreateUserRequest userRequest) {

        User user = new User();
        user.setUsername(userRequest.getUsername());

        Role role = roleService.findByName("ROLE_SVC_USER");
        user.getRoleSet().add(role);

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse createWithTenantId(UUID tenantId, CreateUserRequest userRequest) {

        User user = new User();
        user.setUsername(userRequest.getUsername());

        Tenant tenant = tenantService.findById(tenantId);
        user.setTenantId(tenant.getId());

        Role role = roleService.findByName("ROLE_TENANT_USER");
        user.getRoleSet().add(role);

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return UserResponse.from(user);

    }

    @Transactional
    public void delete(UUID tenantId, String username) {
        User user = findByUsernameAndTenantId(tenantId, username);
        userRepository.delete(user);
    }
}
