package com.financeops.billing.api;

import com.financeops.billing.api.dto.BillingResponses;
import com.financeops.billing.contract.BillingFacts;

final class BillingResponseMapper {
    private BillingResponseMapper() {
    }

    static BillingResponses.InvoiceView invoice(BillingFacts.InvoiceView invoice) {
        return new BillingResponses.InvoiceView(invoice.id(), invoice.customerId(), invoice.customerName(),
            invoice.customerEmail(), invoice.issuedOn(), invoice.dueOn(), invoice.currency(), invoice.total(),
            invoice.settled(), invoice.pending(), invoice.outstanding(), invoice.status(), invoice.overdue(),
            invoice.lines().stream().map(line -> new BillingResponses.LineView(line.description(), line.quantity(),
                line.unitPrice(), line.amount())).toList(), invoice.payments().stream().map(BillingResponseMapper::payment).toList());
    }

    static BillingResponses.PaymentView payment(BillingFacts.PaymentView payment) {
        return new BillingResponses.PaymentView(payment.id(), payment.invoiceId(), payment.customerName(), payment.amount(),
            payment.currency(), payment.status(), payment.method(), payment.recordedOn(), payment.settledOn(),
            payment.events().stream().map(event -> new BillingResponses.EventView(event.id(), event.occurredOn(), event.description())).toList());
    }

    static BillingResponses.CustomerView customer(BillingFacts.CustomerView customer) {
        return new BillingResponses.CustomerView(customer.id(), customer.name(), customer.email(), customer.invoiceCount(), customer.outstanding());
    }

    static BillingResponses.Dashboard dashboard(BillingFacts.Dashboard dashboard) {
        return new BillingResponses.Dashboard(dashboard.workspace(), dashboard.referenceDate(), dashboard.invoiced(),
            dashboard.settled(), dashboard.pending(), dashboard.outstanding(), dashboard.overdue(), dashboard.invoiceCount(), dashboard.customerCount());
    }
}
