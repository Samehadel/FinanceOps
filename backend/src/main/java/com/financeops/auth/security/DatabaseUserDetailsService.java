package com.financeops.auth.security;

import com.financeops.auth.application.UserService;
import com.financeops.auth.domain.UserAccount;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserService users;

    public DatabaseUserDetailsService(UserService users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserAccount user = users.findAccount(email).orElseThrow(() -> new UsernameNotFoundException("Unknown account"));
        return User.withUsername(user.email()).password(user.passwordHash()).roles(user.role()).build();
    }
}
