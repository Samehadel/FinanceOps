package com.financeops.billing.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.financeops.billing.contract.BillingFacts.PaymentView;

/** Authorized stored records before financial calculations; persistence types never cross this boundary. */
public record InvoiceSnapshot(String id, String customerId, String customerName, String customerEmail,
                              LocalDate issuedOn, LocalDate dueOn, String currency,
                              List<Line> lines, List<PaymentView> payments) {
    public InvoiceSnapshot {
        lines = List.copyOf(lines);
        payments = List.copyOf(payments);
    }

    public record Line(String description, int quantity, BigDecimal unitPrice) {
    }
}
