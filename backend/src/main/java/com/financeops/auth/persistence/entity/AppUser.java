package com.financeops.auth.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    private UUID id;
    private String email;
    private String name;
    private String passwordHash;
    private String tenantId;
    private String role;

    protected AppUser() {
    }

    public AppUser(com.financeops.auth.domain.UserAccount account) {
        this.id = account.id();
        this.email = account.email();
        this.name = account.name();
        this.passwordHash = account.passwordHash();
        this.tenantId = account.tenantId();
        this.role = account.role();
    }
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getRole() {
        return role;
    }

}
