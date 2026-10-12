package com.financeops.billing.application;

import com.financeops.billing.contract.BillingFacts.CustomerView;
import com.financeops.billing.contract.BillingFacts.Dashboard;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.billing.contract.BillingQueries;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class BillingQueryFacade implements BillingQueries {
    private final DashboardQueryService dashboard;
    private final CustomerQueryService customers;
    private final InvoiceQueryService invoices;
    private final TransactionQueryService transactions;

    BillingQueryFacade(DashboardQueryService dashboard, CustomerQueryService customers,
                       InvoiceQueryService invoices, TransactionQueryService transactions) {
        this.dashboard = dashboard;
        this.customers = customers;
        this.invoices = invoices;
        this.transactions = transactions;
    }

    @Override
    public Dashboard dashboard() {
        return dashboard.summary();
    }

    @Override
    public List<CustomerView> customers() {
        return customers.all();
    }

    @Override
    public List<InvoiceView> invoices() {
        return invoices.all();
    }

    @Override
    public InvoiceView invoice(String id) {
        return invoices.find(id);
    }

    @Override
    public List<PaymentView> transactions() {
        return transactions.all();
    }

    @Override
    public PaymentView transaction(String id) {
        return transactions.find(id);
    }
}
