package com.kkansal.gatekeeper.management.repository;

import com.kkansal.gatekeeper.management.entity.Tenant;
import com.kkansal.gatekeeper.management.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "roleSet")
    Optional<User> findByUsernameAndTenant(String username, Tenant tenant);

    @EntityGraph(attributePaths = {"roleSet", "roleSet.permissionSet"})
    Optional<User> findByUsername(String username);
}
