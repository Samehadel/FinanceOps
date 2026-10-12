package com.financeops.billing.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class BillingResponses {
    private BillingResponses() {
    }

    public record CustomerView(String id, String name, String email, int invoiceCount, BigDecimal outstanding) {
    }

    public record LineView(String description, int quantity, BigDecimal unitPrice, BigDecimal amount) {
    }

    public record EventView(String id, LocalDate occurredOn, String description) {
    }

    public record PaymentView(String id, String invoiceId, String customerName, BigDecimal amount,
                              String currency, String status, String method, LocalDate recordedOn,
                              LocalDate settledOn, List<EventView> events) {
    }

    public record InvoiceView(String id, String customerId, String customerName, String customerEmail,
                              LocalDate issuedOn, LocalDate dueOn, String currency, BigDecimal total,
                              BigDecimal settled, BigDecimal pending, BigDecimal outstanding,
                              String status, boolean overdue, List<LineView> lines, List<PaymentView> payments) {
    }

    public record Dashboard(String workspace, LocalDate referenceDate, BigDecimal invoiced,
                            BigDecimal settled, BigDecimal pending, BigDecimal outstanding,
                            BigDecimal overdue, int invoiceCount, int customerCount) {
    }
}
