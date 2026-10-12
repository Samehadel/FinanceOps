package com.financeops.auth.domain;

import java.util.UUID;

/** Internal account data; never returned by an HTTP controller. */
public record UserAccount(UUID id, String email, String name, String passwordHash, String tenantId, String role) {
}
