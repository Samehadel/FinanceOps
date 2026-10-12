package com.financeops.billing.persistence;

import com.financeops.billing.contract.BillingFacts.EventView;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.billing.persistence.entity.Customer;
import com.financeops.billing.persistence.entity.Invoice;
import com.financeops.billing.persistence.entity.Payment;
import com.financeops.billing.persistence.repository.CustomerRepository;
import com.financeops.billing.persistence.repository.InvoiceRepository;
import com.financeops.billing.persistence.repository.PaymentEventRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
class PaymentFactsMapper {
    private final CustomerRepository customers;
    private final InvoiceRepository invoices;
    private final PaymentEventRepository events;

    PaymentFactsMapper(CustomerRepository customers, InvoiceRepository invoices, PaymentEventRepository events) {
        this.customers = customers;
        this.invoices = invoices;
        this.events = events;
    }

    PaymentView map(Payment payment) {
        Invoice invoice = invoices.findByIdAndTenantId(payment.getInvoiceId(), payment.getTenantId()).orElseThrow();
        Customer customer = customers.findByIdAndTenantId(invoice.getCustomerId(), payment.getTenantId()).orElseThrow();
        List<EventView> history = events.findByPaymentIdAndTenantIdOrderByOccurredOn(payment.getId(), payment.getTenantId())
            .stream().map(event -> new EventView(event.getId(), event.getOccurredOn(), event.getDescription())).toList();
        return new PaymentView(payment.getId(), payment.getInvoiceId(), customer.getName(), payment.getAmount(),
            invoice.getCurrency(), payment.getStatus(), payment.getMethod(), payment.getRecordedOn(), payment.getSettledOn(), history);
    }
}
