package com.financeops.billing.persistence.seed;

import com.financeops.auth.spi.WorkspaceProvisioner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcPracticeWorkspaceProvisioner implements WorkspaceProvisioner {
    private final JdbcTemplate jdbc;

    public JdbcPracticeWorkspaceProvisioner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void provision(String tenantId, String ownerName, String suffix) {
        jdbc.update("INSERT INTO tenant VALUES (?, ?)", tenantId, ownerName + "'s workspace");
        createPracticeRecords(tenantId, suffix);
        createExpandedRecords(tenantId);
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
