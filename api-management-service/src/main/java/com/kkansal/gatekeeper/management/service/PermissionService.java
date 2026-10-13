package com.kkansal.gatekeeper.management.service;

import com.kkansal.gatekeeper.management.dto.request.PermissionRequest;
import com.kkansal.gatekeeper.management.dto.response.PermissionResponse;
import com.kkansal.gatekeeper.management.entity.Permission;
import com.kkansal.gatekeeper.management.exception.DuplicateResourceException;
import com.kkansal.gatekeeper.management.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PermissionService {

    private PermissionRepository permissionRepository;

    public Permission findByName(String name) {

        Permission permission = permissionRepository.findByName(name);

        if(permission == null)
            throw new EntityNotFoundException("No permission exists with name - " + name);

        return permission;
    }

    @Transactional
    public PermissionResponse create(PermissionRequest permissionRequest) {

        if(permissionRepository.findByName(permissionRequest.getName()) != null) {
            throw new DuplicateResourceException("Permission already exists with name - " + permissionRequest.getName());
        }

        Permission permission = Permission.builder()
                .name(permissionRequest.getName())
                .build();

        permissionRepository.save(permission);

        return PermissionResponse.from(permission);
    }

    public List<PermissionResponse> get() {

        return permissionRepository.findAll().stream()
                .map(PermissionResponse::from)
                .toList();
    }

    @Transactional
    public PermissionResponse update(String name, PermissionRequest permissionRequest) {

        Permission permission = findByName(name);

        if(permissionRepository.findByName(permissionRequest.getName()) != null) {
            throw new DuplicateResourceException("Permission already exists with name - " + permissionRequest.getName());
        }

        permission.setName(permissionRequest.getName());
        return PermissionResponse.from(permission);
    }

    @Transactional
    public void delete(String name) {

        Permission permission = findByName(name);
        permissionRepository.delete(permission);
    }
}
