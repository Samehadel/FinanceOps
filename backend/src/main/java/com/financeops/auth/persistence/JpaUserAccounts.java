package com.financeops.auth.persistence;

import com.financeops.auth.application.port.UserAccounts;
import com.financeops.auth.domain.UserAccount;
import com.financeops.auth.persistence.entity.AppUser;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class JpaUserAccounts implements UserAccounts {
    private final UserRepository users;

    public JpaUserAccounts(UserRepository users) {
        this.users = users;
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return users.findByEmail(email).map(user -> new UserAccount(user.getId(), user.getEmail(),
            user.getName(), user.getPasswordHash(), user.getTenantId(), user.getRole()));
    }

    @Override
    public void save(UserAccount account) {
        users.save(new AppUser(account));
    }
}
