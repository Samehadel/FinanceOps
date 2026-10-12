package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
class Customer {
    @Id
    String id;
    String tenantId;
    String name;
    String email;
}
