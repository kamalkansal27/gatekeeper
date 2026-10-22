package com.kkansal.gatekeeper.management.repository;

import com.kkansal.gatekeeper.management.entity.Role;
import com.kkansal.gatekeeper.management.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByNameAndTenantIsNull(String name);

    Optional<Role> findByNameAndTenant(String name, Tenant tenant);

    Optional<Role> findByIdAndTenant(UUID id, Tenant tenant);

    boolean existsByNameAndTenant(String name, Tenant tenant);

    boolean existsByNameAndTenantIsNull(String name);
}
