package com.financeops.billing;

import com.financeops.auth.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import static com.financeops.billing.BillingViews.*;

/** Every public read derives its tenant from the authenticated identity, never request parameters. */
@Service
@Transactional(readOnly = true)
public class BillingService {
    private final UserService users;
    private final TenantRepository tenants;
    private final CustomerRepository customers;
    private final InvoiceRepository invoices;
    private final InvoiceLineRepository lines;
    private final PaymentRepository payments;
    private final PaymentEventRepository events;
    private final LocalDate referenceDate;

    public BillingService(UserService users, TenantRepository tenants, CustomerRepository customers,
                          InvoiceRepository invoices, InvoiceLineRepository lines, PaymentRepository payments,
                          PaymentEventRepository events, @Value("${financeops.reference-date}") LocalDate referenceDate) {
        this.users = users;
        this.tenants = tenants;
        this.customers = customers;
        this.invoices = invoices;
        this.lines = lines;
        this.payments = payments;
        this.events = events;
        this.referenceDate = referenceDate;
    }

    public Dashboard dashboard(String email) {
        String tenant = tenant(email);
        List<InvoiceView> records = invoices(email);
        BigDecimal overdue = records.stream().filter(InvoiceView::overdue)
            .map(InvoiceView::outstanding).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dashboard(tenants.findById(tenant).orElseThrow().name, referenceDate,
            sum(records, InvoiceView::total), sum(records, InvoiceView::settled),
            sum(records, InvoiceView::pending), sum(records, InvoiceView::outstanding), overdue,
            records.size(), customers.findByTenantIdOrderByName(tenant).size());
    }

    public List<InvoiceView> invoices(String email) {
        String tenant = tenant(email);
        return invoices.findByTenantIdOrderByIssuedOnDesc(tenant).stream().map(this::invoiceView).toList();
    }

    public InvoiceView invoice(String email, String id) {
        return invoiceView(invoices.findByIdAndTenantId(id, tenant(email)).orElseThrow(BillingService::notFound));
    }

    public List<CustomerView> customers(String email) {
        String tenant = tenant(email);
        List<InvoiceView> records = invoices(email);
        return customers.findByTenantIdOrderByName(tenant).stream().map(customer -> {
            List<InvoiceView> owned = records.stream().filter(invoice -> invoice.customerId().equals(customer.id)).toList();
            return new CustomerView(customer.id, customer.name, customer.email, owned.size(), sum(owned, InvoiceView::outstanding));
        }).toList();
    }

    public List<PaymentView> transactions(String email) {
        return payments.findByTenantIdOrderByRecordedOnDesc(tenant(email)).stream().map(this::paymentView).toList();
    }

    public PaymentView transaction(String email, String id) {
        return paymentView(payments.findByIdAndTenantId(id, tenant(email)).orElseThrow(BillingService::notFound));
    }

    private InvoiceView invoiceView(Invoice invoice) {
        Customer customer = customers.findByIdAndTenantId(invoice.customerId, invoice.tenantId).orElseThrow();
        List<LineView> items = lines.findByInvoiceIdOrderById(invoice.id).stream()
            .map(line -> new LineView(line.description, line.quantity, line.unitPrice,
                line.unitPrice.multiply(BigDecimal.valueOf(line.quantity)))).toList();
        List<PaymentView> records = payments.findByInvoiceIdAndTenantIdOrderByRecordedOnDesc(invoice.id, invoice.tenantId)
            .stream().map(this::paymentView).toList();
        BigDecimal total = items.stream().map(LineView::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal settled = paymentSum(records, "SETTLED");
        BigDecimal pending = paymentSum(records, "PENDING");
        BigDecimal outstanding = total.subtract(settled).max(BigDecimal.ZERO);
        boolean overdue = outstanding.signum() > 0 && invoice.dueOn.isBefore(referenceDate);
        String status = outstanding.signum() == 0 ? "PAID" : overdue ? "OVERDUE" : "OPEN";
        return new InvoiceView(invoice.id, customer.id, customer.name, customer.email, invoice.issuedOn,
            invoice.dueOn, invoice.currency, total, settled, pending, outstanding, status, overdue, items, records);
    }

    private PaymentView paymentView(Payment payment) {
        Invoice invoice = invoices.findByIdAndTenantId(payment.invoiceId, payment.tenantId).orElseThrow();
        Customer customer = customers.findByIdAndTenantId(invoice.customerId, payment.tenantId).orElseThrow();
        List<EventView> history = events.findByPaymentIdAndTenantIdOrderByOccurredOn(payment.id, payment.tenantId)
            .stream().map(event -> new EventView(event.id, event.occurredOn, event.description)).toList();
        return new PaymentView(payment.id, payment.invoiceId, customer.name, payment.amount, invoice.currency,
            payment.status, payment.method, payment.recordedOn, payment.settledOn, history);
    }

    private String tenant(String email) {
        return users.requireUser(email).tenantId();
    }

    private BigDecimal sum(List<InvoiceView> records, Function<InvoiceView, BigDecimal> value) {
        return records.stream().map(value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal paymentSum(List<PaymentView> records, String status) {
        return records.stream().filter(payment -> payment.status().equals(status))
            .map(PaymentView::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No accessible matching record.");
    }
}
