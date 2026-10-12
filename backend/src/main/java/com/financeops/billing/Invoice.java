package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
class Invoice {
    @Id
    String id;
    String tenantId;
    String customerId;
    LocalDate issuedOn;
    LocalDate dueOn;
    String currency;
}
