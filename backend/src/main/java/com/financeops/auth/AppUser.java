package com.financeops.auth;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    UUID id;
    String email;
    String name;
    String passwordHash;
    String tenantId;
    String role;

    protected AppUser() {
    }

    public AppUser(String email, String name, String passwordHash, String tenantId) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.tenantId = tenantId;
        this.role = "SUPPORT";
    }

    public String tenantId() {
        return tenantId;
    }
}
