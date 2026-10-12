package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line")
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invoiceId;
    private String description;
    private int quantity;
    @Column(precision = 14, scale = 2)
    private BigDecimal unitPrice;
    protected InvoiceLine() {
    }

    public Long getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

}
