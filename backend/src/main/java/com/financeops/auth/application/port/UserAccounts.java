package com.financeops.auth.application.port;

import com.financeops.auth.domain.UserAccount;
import java.util.Optional;

public interface UserAccounts {
    Optional<UserAccount> findByEmail(String email);
    void save(UserAccount account);
}
