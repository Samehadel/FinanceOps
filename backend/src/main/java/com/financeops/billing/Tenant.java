package com.financeops.billing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tenant")
class Tenant {
    @Id
    String id;
    String name;
}
