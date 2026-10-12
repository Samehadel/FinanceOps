package com.financeops.auth;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        AppUser user = requireUser(email);
        return User.withUsername(user.email).password(user.passwordHash).roles(user.role).build();
    }

    public AppUser requireUser(String email) {
        return users.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("Unknown account"));
    }

    public Profile profile(String email) {
        AppUser user = requireUser(email);
        return new Profile(user.id.toString(), user.name, user.email, user.tenantId, user.role);
    }

    public static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    public record Profile(String id, String name, String email, String tenantId, String role) {
    }
}
