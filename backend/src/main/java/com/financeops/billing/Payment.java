package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
class Payment {
    @Id
    String id;
    String tenantId;
    String invoiceId;
    @Column(precision = 14, scale = 2)
    BigDecimal amount;
    String status;
    LocalDate recordedOn;
    LocalDate settledOn;
    String method;
}
