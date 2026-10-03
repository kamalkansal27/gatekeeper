package com.kkansal.gatekeeper.management.entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> permissionList = new ArrayList<>();

        for (Role role : user.getRoleSet()) {
            permissionList.add(new SimpleGrantedAuthority(role.getName()));
            for(Permission permission : role.getPermissionSet()) {
                permissionList.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return permissionList;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Tenant getTenant() {
        return user.getTenant();
    }
}
