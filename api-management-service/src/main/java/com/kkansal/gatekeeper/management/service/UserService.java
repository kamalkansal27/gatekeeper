package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateUserRequest;
import com.kkansal.gatekeeper.management.dto.response.UserResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;


@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleService roleService;
    private TenantService tenantService;
    private PasswordEncoder passwordEncoder;

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new EntityNotFoundException("User not found with: " + email));
    }

    public User findByUsernameAndTenant(Tenant tenant, String username) {
        return userRepository.findByUsernameAndTenant(username, tenant)
                .orElseThrow(() -> new EntityNotFoundException("User not found with: " + username));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse get(UUID tenantId, String username) {

        Tenant tenant = tenantService.findById(tenantId);

        User user = findByUsernameAndTenant(tenant, username);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse create(CreateUserRequest userRequest) {

        if(userRequest.getTenantId() != null) {
            return createWithTenantId(userRequest.getTenantId(), userRequest);
        }

        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateResourceException("User with email already exists " + userRequest.getEmail());
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(uniqueEmail(userRequest.getEmail()));

        Role role = roleService.findByName("ROLE_ADMIN");
        user.getRoleSet().add(role);

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse createWithTenantId(UUID tenantId, CreateUserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateResourceException("User with email already exists " + userRequest.getEmail());
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(uniqueEmail(userRequest.getEmail()));

        Tenant tenant = tenantService.findById(tenantId);
        user.setTenant(tenant);

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return UserResponse.from(user);

    }

    @Transactional
    public void delete(UUID tenantId, String username) {

        Tenant tenant = tenantService.findById(tenantId);
        User user = findByUsernameAndTenant(tenant, username);
        userRepository.delete(user);
    }

    private String uniqueEmail(String email) {
        String normalized = normalizeEmail(email);
        if (userRepository.existsByEmail(normalized)) {
            throw new DuplicateResourceException("User with email '" + normalized + "' already exists");
        }
        return normalized;
    }
}
