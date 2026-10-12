package com.financeops.billing.persistence;

import com.financeops.billing.application.port.BillingRecords;
import com.financeops.billing.domain.CustomerRecord;
import com.financeops.billing.domain.InvoiceSnapshot;
import com.financeops.billing.contract.BillingFacts.EventView;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.billing.persistence.entity.Customer;
import com.financeops.billing.persistence.entity.Invoice;
import com.financeops.billing.persistence.entity.Payment;
import com.financeops.billing.persistence.repository.CustomerRepository;
import com.financeops.billing.persistence.repository.InvoiceLineRepository;
import com.financeops.billing.persistence.repository.InvoiceRepository;
import com.financeops.billing.persistence.repository.PaymentEventRepository;
import com.financeops.billing.persistence.repository.PaymentRepository;
import com.financeops.billing.persistence.repository.TenantRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/** Maps tenant-scoped database records to detached immutable domain snapshots. */
@Repository
public class JpaBillingRecords implements BillingRecords {
    private final TenantRepository tenants;
    private final CustomerRepository customers;
    private final InvoiceRepository invoices;
    private final InvoiceLineRepository lines;
    private final PaymentRepository payments;
    private final PaymentEventRepository events;

    public JpaBillingRecords(TenantRepository tenants, CustomerRepository customers, InvoiceRepository invoices,
                             InvoiceLineRepository lines, PaymentRepository payments, PaymentEventRepository events) {
        this.tenants = tenants;
        this.customers = customers;
        this.invoices = invoices;
        this.lines = lines;
        this.payments = payments;
        this.events = events;
    }

    @Override
    public List<InvoiceSnapshot> invoices(String tenantId) {
        return invoices.findByTenantIdOrderByIssuedOnDesc(tenantId).stream().map(this::snapshot).toList();
    }

    @Override
    public Optional<InvoiceSnapshot> invoice(String tenantId, String id) {
        return invoices.findByIdAndTenantId(id, tenantId).map(this::snapshot);
    }

    @Override
    public List<PaymentView> payments(String tenantId) {
        return payments.findByTenantIdOrderByRecordedOnDesc(tenantId).stream().map(this::paymentFacts).toList();
    }

    @Override
    public Optional<PaymentView> payment(String tenantId, String id) {
        return payments.findByIdAndTenantId(id, tenantId).map(this::paymentFacts);
    }

    @Override
    public List<CustomerRecord> customers(String tenantId) {
        return customers.findByTenantIdOrderByName(tenantId).stream()
            .map(customer -> new CustomerRecord(customer.getId(), customer.getName(), customer.getEmail())).toList();
    }

    @Override
    public String workspaceName(String tenantId) {
        return tenants.findById(tenantId).orElseThrow().getName();
    }

    private InvoiceSnapshot snapshot(Invoice invoice) {
        Customer customer = customers.findByIdAndTenantId(invoice.getCustomerId(), invoice.getTenantId()).orElseThrow();
        List<InvoiceSnapshot.Line> items = lines.findScopedLines(invoice.getId(), invoice.getTenantId()).stream()
            .map(line -> new InvoiceSnapshot.Line(line.getDescription(), line.getQuantity(), line.getUnitPrice())).toList();
        List<PaymentView> associated = payments.findByInvoiceIdAndTenantIdOrderByRecordedOnDesc(invoice.getId(), invoice.getTenantId())
            .stream().map(this::paymentFacts).toList();
        return new InvoiceSnapshot(invoice.getId(), customer.getId(), customer.getName(), customer.getEmail(),
            invoice.getIssuedOn(), invoice.getDueOn(), invoice.getCurrency(), items, associated);
    }

    private PaymentView paymentFacts(Payment payment) {
        Invoice invoice = invoices.findByIdAndTenantId(payment.getInvoiceId(), payment.getTenantId()).orElseThrow();
        Customer customer = customers.findByIdAndTenantId(invoice.getCustomerId(), payment.getTenantId()).orElseThrow();
        List<EventView> history = events.findByPaymentIdAndTenantIdOrderByOccurredOn(payment.getId(), payment.getTenantId())
            .stream().map(event -> new EventView(event.getId(), event.getOccurredOn(), event.getDescription())).toList();
        return new PaymentView(payment.getId(), payment.getInvoiceId(), customer.getName(), payment.getAmount(),
            invoice.getCurrency(), payment.getStatus(), payment.getMethod(), payment.getRecordedOn(), payment.getSettledOn(), history);
    }
}
