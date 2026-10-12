package com.financeops.auth.bootstrap;

import com.financeops.auth.application.port.UserAccounts;
import com.financeops.auth.domain.UserAccount;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoUserInitializer implements CommandLineRunner {
    private final UserAccounts users;
    private final PasswordEncoder encoder;
    private final String password;

    public DemoUserInitializer(UserAccounts users, PasswordEncoder encoder,
                               @Value("${financeops.demo-password}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        createIfMissing("maya@financeops.local", "Maya", "T-A");
        createIfMissing("omar@financeops.local", "Omar", "T-B");
    }

    private void createIfMissing(String email, String name, String tenantId) {
        if (users.findByEmail(email).isEmpty()) {
            users.save(new UserAccount(UUID.randomUUID(), email, name, encoder.encode(password), tenantId, "SUPPORT"));
        }
    }
}
