package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private String id;
    private String tenantId;
    private String name;
    private String email;
    protected Customer() {
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}
