package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_line")
class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String invoiceId;
    String description;
    int quantity;
    @Column(precision = 14, scale = 2)
    BigDecimal unitPrice;
}
