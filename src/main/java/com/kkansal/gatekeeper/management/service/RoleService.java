package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.CreateRoleRequest;
import com.kkansal.gatekeeper.management.dto.request.UpdateRoleRequest;
import com.kkansal.gatekeeper.management.dto.response.RoleResponse;
import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleService {

    private RoleRepository roleRepository;

    public Role findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID : " + id));
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name : " + name));
    }

    public RoleResponse get(UUID roleId) {
        return RoleResponse.from(findById(roleId));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest roleRequest) {

        Role role = Role.builder()
                .name(roleRequest.getName())
                .build();

        roleRepository.save(role);
        return RoleResponse.from(role);
    }

    @Transactional
    public RoleResponse update(UUID roleId, UpdateRoleRequest roleRequest) {

        Role role = findById(roleId);
        role.setName(roleRequest.getName() != null ? roleRequest.getName() : role.getName());
        return RoleResponse.from(role);
    }

    @Transactional
    public void delete(UUID roleId) {
        Role role = findById(roleId);
        roleRepository.delete(role);
    }


}
