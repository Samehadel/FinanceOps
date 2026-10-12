package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "tenant")
public class Tenant {
    @Id
    private String id;
    private String name;
    protected Tenant() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
