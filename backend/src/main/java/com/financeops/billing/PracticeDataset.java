package com.financeops.billing;

import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Shared fictional fixtures for the Flyway upgrade and future isolated signups. */
public final class PracticeDataset {
    private PracticeDataset() {
    }

    /** Null tenant seeds every existing workspace; a tenant ID seeds only that workspace. */
    public static void seed(Connection connection, String tenantId) throws SQLException, IOException {
        String sql;
        try (var input = new ClassPathResource("db/practice/expanded-records.sql").getInputStream()) {
            sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        // The owned fixture resource contains plain INSERT statements, with no embedded semicolons.
        for (String statement : sql.split(";")) {
            if (statement.isBlank()) {
                continue;
            }
            try (PreparedStatement prepared = connection.prepareStatement(statement)) {
                prepared.setString(1, tenantId);
                prepared.executeUpdate();
            }
        }
    }
}
