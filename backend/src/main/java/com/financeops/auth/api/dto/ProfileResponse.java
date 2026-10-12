package com.financeops.auth.api.dto;

import com.financeops.auth.contract.UserProfile;

public record ProfileResponse(String id, String name, String email, String tenantId, String role) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(profile.id(), profile.name(), profile.email(), profile.tenantId(), profile.role());
    }
}
