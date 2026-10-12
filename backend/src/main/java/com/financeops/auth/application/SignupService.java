package com.financeops.auth.application;

import com.financeops.auth.application.port.UserAccounts;
import com.financeops.auth.spi.WorkspaceProvisioner;
import com.financeops.auth.domain.UserAccount;
import com.financeops.shared.domain.ConflictException;
import com.financeops.shared.domain.InvalidRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Account creation and workspace provisioning commit atomically; clients cannot select scope or role. */
@Service
public class SignupService {
    private final UserAccounts users;
    private final WorkspaceProvisioner workspaces;
    private final PasswordEncoder encoder;

    public SignupService(UserAccounts users, WorkspaceProvisioner workspaces, PasswordEncoder encoder) {
        this.users = users;
        this.workspaces = workspaces;
        this.encoder = encoder;
    }

    @Transactional
    public void signup(String name, String email, String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidRequestException("Password is too long when encoded. Use a shorter password.");
        }
        String normalized = UserService.normalizeEmail(email);
        if (users.findByEmail(normalized).isPresent()) {
            throw new ConflictException("Account could not be created. Try a different email.");
        }
        String suffix = UUID.randomUUID().toString();
        String tenantId = "T-" + suffix;
        workspaces.provision(tenantId, name.strip(), suffix);
        users.save(new UserAccount(UUID.randomUUID(), normalized, name.strip(), encoder.encode(password), tenantId, "SUPPORT"));
    }
}
