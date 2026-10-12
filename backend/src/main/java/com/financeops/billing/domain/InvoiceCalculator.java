package com.financeops.billing.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import com.financeops.billing.contract.BillingFacts.LineView;
import com.financeops.billing.contract.BillingFacts.PaymentView;

/** Pure financial rules: only settled payments reduce balances; due-today is not overdue. */
public final class InvoiceCalculator {
    private InvoiceCalculator() {
    }

    public static InvoiceView calculate(InvoiceSnapshot invoice, LocalDate referenceDate) {
        List<LineView> lines = invoice.lines().stream().map(line -> new LineView(line.description(),
            line.quantity(), line.unitPrice(), line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())))).toList();
        BigDecimal total = lines.stream().map(LineView::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal settled = paymentSum(invoice.payments(), "SETTLED");
        BigDecimal pending = paymentSum(invoice.payments(), "PENDING");
        BigDecimal outstanding = total.subtract(settled).max(BigDecimal.ZERO);
        boolean overdue = outstanding.signum() > 0 && invoice.dueOn().isBefore(referenceDate);
        String status = outstanding.signum() == 0 ? "PAID" : overdue ? "OVERDUE" : "OPEN";
        return new InvoiceView(invoice.id(), invoice.customerId(), invoice.customerName(), invoice.customerEmail(),
            invoice.issuedOn(), invoice.dueOn(), invoice.currency(), total, settled, pending, outstanding,
            status, overdue, lines, invoice.payments());
    }

    private static BigDecimal paymentSum(List<PaymentView> payments, String status) {
        return payments.stream().filter(payment -> payment.status().equals(status))
            .map(PaymentView::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
