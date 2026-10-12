package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    private String id;
    private String tenantId;
    private String customerId;
    private LocalDate issuedOn;
    private LocalDate dueOn;
    private String currency;
    protected Invoice() {
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LocalDate getIssuedOn() {
        return issuedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public String getCurrency() {
        return currency;
    }

}
