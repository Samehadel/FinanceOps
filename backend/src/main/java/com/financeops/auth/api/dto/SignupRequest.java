package com.financeops.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(@NotBlank @Size(max = 100) String name,
                            @NotBlank @Email @Size(max = 254) String email,
                            @NotBlank @Size(min = 10, max = 64) String password) {
}
