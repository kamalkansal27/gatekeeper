package com.kkansal.gatekeeper.management.repository;

import com.kkansal.gatekeeper.management.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Permission findByName(String name);
}
