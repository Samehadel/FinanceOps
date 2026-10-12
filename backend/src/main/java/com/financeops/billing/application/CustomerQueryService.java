package com.financeops.billing.application;

import com.financeops.auth.contract.CurrentUser;
import com.financeops.billing.application.port.BillingRecords;
import com.financeops.billing.contract.BillingFacts.CustomerView;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerQueryService {
    private final CurrentUser access;
    private final BillingRecords records;
    private final InvoiceQueryService invoices;

    public CustomerQueryService(CurrentUser access, BillingRecords records, InvoiceQueryService invoices) {
        this.access = access;
        this.records = records;
        this.invoices = invoices;
    }

    public List<CustomerView> all() {
        List<InvoiceView> authorized = invoices.all();
        return records.customers(access.currentTenant()).stream().map(customer -> {
            List<InvoiceView> owned = authorized.stream().filter(invoice -> invoice.customerId().equals(customer.id())).toList();
            BigDecimal outstanding = owned.stream().map(InvoiceView::outstanding).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new CustomerView(customer.id(), customer.name(), customer.email(), owned.size(), outstanding);
        }).toList();
    }
}
