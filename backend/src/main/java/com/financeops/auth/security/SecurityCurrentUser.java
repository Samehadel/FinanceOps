package com.financeops.auth.security;

import com.financeops.auth.application.UserService;
import com.financeops.auth.contract.UserProfile;
import com.financeops.auth.domain.UserAccount;
import com.financeops.auth.contract.CurrentUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUser implements CurrentUser {
    private final UserService users;

    public SecurityCurrentUser(UserService users) {
        this.users = users;
    }

    @Override
    public String currentTenant() {
        return account().tenantId();
    }

    @Override
    public UserProfile profile() {
        UserAccount user = account();
        return new UserProfile(user.id().toString(), user.name(), user.email(), user.tenantId(), user.role());
    }

    private UserAccount account() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Please sign in.");
        }
        return users.findAccount(authentication.getName())
            .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Please sign in."));
    }
}
