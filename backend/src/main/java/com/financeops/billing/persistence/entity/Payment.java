package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    private String id;
    private String tenantId;
    private String invoiceId;
    @Column(precision = 14, scale = 2)
    private BigDecimal amount;
    private String status;
    private LocalDate recordedOn;
    private LocalDate settledOn;
    private String method;
    protected Payment() {
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public LocalDate getSettledOn() {
        return settledOn;
    }

    public String getMethod() {
        return method;
    }

}
