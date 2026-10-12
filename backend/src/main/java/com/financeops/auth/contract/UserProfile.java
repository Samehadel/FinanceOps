package com.financeops.auth.contract;

public record UserProfile(String id, String name, String email, String tenantId, String role) {
}
