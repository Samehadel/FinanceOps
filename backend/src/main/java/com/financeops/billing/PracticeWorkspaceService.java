package com.financeops.billing;

import com.financeops.auth.AppUser;
import com.financeops.auth.UserRepository;
import com.financeops.auth.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

/** Signup always creates a new tenant; clients cannot choose an existing tenant or role. */
@Service
public class PracticeWorkspaceService {
    private final JdbcTemplate jdbc;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public PracticeWorkspaceService(JdbcTemplate jdbc, UserRepository users, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public void signup(String name, String email, String password) {
        if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long when encoded. Use a shorter password.");
        }
        String normalized = UserService.normalizeEmail(email);
        if (users.findByEmail(normalized).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account could not be created. Try a different email.");
        }
        String suffix = UUID.randomUUID().toString();
        String tenantId = "T-" + suffix;
        jdbc.update("INSERT INTO tenant VALUES (?, ?)", tenantId, name + "'s workspace");
        createPracticeRecords(tenantId, suffix);
        createExpandedRecords(tenantId);
        users.save(new AppUser(normalized, name, encoder.encode(password), tenantId));
    }

    private void createExpandedRecords(String tenantId) {
        jdbc.execute((ConnectionCallback<Void>) connection -> {
            try {
                PracticeDataset.seed(connection, tenantId);
            } catch (java.io.IOException exception) {
                throw new IllegalStateException("Practice fixtures could not be loaded", exception);
            }
            return null;
        });
    }

    private void createPracticeRecords(String tenantId, String suffix) {
        String customerId = "C-" + suffix;
        String invoiceId = "INV-" + suffix;
        String paymentId = "PAY-" + suffix;
        jdbc.update("INSERT INTO customer VALUES (?, ?, ?, ?)", customerId, tenantId,
            "Practice Company", "billing@practice.example");
        jdbc.update("INSERT INTO invoice VALUES (?, ?, ?, '2026-10-01', '2026-10-15', 'USD')",
            invoiceId, tenantId, customerId);
        jdbc.update("INSERT INTO invoice_line (invoice_id, description, quantity, unit_price) VALUES (?, ?, 1, 120.00)",
            invoiceId, "Practice subscription");
        jdbc.update("INSERT INTO payment VALUES (?, ?, ?, 70.00, 'SETTLED', '2026-10-05', '2026-10-05', 'Bank transfer')",
            paymentId, tenantId, invoiceId);
        jdbc.update("INSERT INTO payment_event VALUES (?, ?, ?, '2026-10-05', 'Practice payment became settled')",
            "EV-" + suffix, tenantId, paymentId);
    }
}
