package com.kkansal.gatekeeper.management.service.auth;

import com.kkansal.gatekeeper.management.entity.CustomUserDetails;
import com.kkansal.gatekeeper.management.entity.User;
import com.kkansal.gatekeeper.management.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username);

        return new CustomUserDetails(user);
    }
}
