package com.financeops.auth.application;

import com.financeops.auth.application.port.UserAccounts;
import com.financeops.auth.domain.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserAccounts users;

    public UserService(UserAccounts users) {
        this.users = users;
    }

    public Optional<UserAccount> findAccount(String email) {
        return users.findByEmail(normalizeEmail(email));
    }

    public static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
