package com.financeops.billing.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
@Table(name = "payment_event")
public class PaymentEvent {
    @Id
    private String id;
    private String tenantId;
    private String paymentId;
    private LocalDate occurredOn;
    private String description;
    protected PaymentEvent() {
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public LocalDate getOccurredOn() {
        return occurredOn;
    }

    public String getDescription() {
        return description;
    }

}
