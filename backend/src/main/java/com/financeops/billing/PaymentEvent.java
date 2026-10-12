package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_event")
class PaymentEvent {
    @Id
    String id;
    String tenantId;
    String paymentId;
    LocalDate occurredOn;
    String description;
}
