package com.financeops.billing.contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.experimental.FieldNameConstants;

public final class BillingFacts {
    private BillingFacts() {
    }

    @FieldNameConstants(asEnum = true)
    public record CustomerView(String id, String name, String email, int invoiceCount, BigDecimal outstanding) {
    }

    public record LineView(String description, int quantity, BigDecimal unitPrice, BigDecimal amount) {
    }

    public record EventView(String id, LocalDate occurredOn, String description) {
    }

    @FieldNameConstants(asEnum = true)
    public record PaymentView(String id, String invoiceId, String customerName, BigDecimal amount,
                              String currency, String status, String method, LocalDate recordedOn,
                              LocalDate settledOn, List<EventView> events) {
        public PaymentView {
            events = List.copyOf(events);
        }
    }

    @FieldNameConstants(asEnum = true)
    public record InvoiceView(String id, String customerId, String customerName, String customerEmail,
                              LocalDate issuedOn, LocalDate dueOn, String currency, BigDecimal total,
                              BigDecimal settled, BigDecimal pending, BigDecimal outstanding,
                              String status, boolean overdue, List<LineView> lines, List<PaymentView> payments) {
        public InvoiceView {
            lines = List.copyOf(lines);
            payments = List.copyOf(payments);
        }
    }

    public record Dashboard(String workspace, LocalDate referenceDate, BigDecimal invoiced,
                            BigDecimal settled, BigDecimal pending, BigDecimal outstanding,
                            BigDecimal overdue, int invoiceCount, int customerCount) {
    }
}
