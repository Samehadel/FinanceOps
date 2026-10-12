package com.financeops.billing.contract;

import com.financeops.billing.contract.BillingFacts.CustomerView;
import com.financeops.billing.contract.BillingFacts.Dashboard;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import java.util.List;

/**
 * Public module API for HTTP and future in-process consumers. Every query derives tenant scope
 * from the authenticated session. Results are financial facts, independent of HTTP and JPA.
 */
public interface BillingQueries {
    Dashboard dashboard();
    List<CustomerView> customers();
    List<InvoiceView> invoices();
    InvoiceView invoice(String id);
    List<PaymentView> transactions();
    PaymentView transaction(String id);
}
