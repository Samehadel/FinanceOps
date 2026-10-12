package com.financeops.billing.persistence;

import com.financeops.billing.application.port.InvoiceRecords;
import com.financeops.billing.domain.InvoiceSnapshot;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.billing.persistence.entity.Customer;
import com.financeops.billing.persistence.entity.Invoice;
import com.financeops.billing.persistence.repository.CustomerRepository;
import com.financeops.billing.persistence.repository.InvoiceLineRepository;
import com.financeops.billing.persistence.repository.InvoiceRepository;
import com.financeops.billing.persistence.repository.PaymentRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaInvoiceRecords implements InvoiceRecords {
    private final CustomerRepository customers;
    private final InvoiceRepository invoices;
    private final InvoiceLineRepository lines;
    private final PaymentRepository payments;
    private final PaymentFactsMapper paymentFacts;

    JpaInvoiceRecords(CustomerRepository customers, InvoiceRepository invoices, InvoiceLineRepository lines,
                      PaymentRepository payments, PaymentFactsMapper paymentFacts) {
        this.customers = customers;
        this.invoices = invoices;
        this.lines = lines;
        this.payments = payments;
        this.paymentFacts = paymentFacts;
    }

    @Override
    public List<InvoiceSnapshot> invoices(String tenantId) {
        return invoices.findByTenantIdOrderByIssuedOnDesc(tenantId).stream().map(this::snapshot).toList();
    }

    @Override
    public Optional<InvoiceSnapshot> invoice(String tenantId, String id) {
        return invoices.findByIdAndTenantId(id, tenantId).map(this::snapshot);
    }

    private InvoiceSnapshot snapshot(Invoice invoice) {
        Customer customer = customers.findByIdAndTenantId(invoice.getCustomerId(), invoice.getTenantId()).orElseThrow();
        List<InvoiceSnapshot.Line> items = lines.findScopedLines(invoice.getId(), invoice.getTenantId()).stream()
            .map(line -> new InvoiceSnapshot.Line(line.getDescription(), line.getQuantity(), line.getUnitPrice())).toList();
        List<PaymentView> associated = payments.findByInvoiceIdAndTenantIdOrderByRecordedOnDesc(invoice.getId(), invoice.getTenantId())
            .stream().map(paymentFacts::map).toList();
        return new InvoiceSnapshot(
                invoice.getId(),
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                invoice.getIssuedOn(),
                invoice.getDueOn(),
                invoice.getCurrency(),
                items,
                associated);
    }
}
