package com.financeops.billing.application.port;

import com.financeops.billing.domain.CustomerRecord;
import com.financeops.billing.domain.InvoiceSnapshot;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import java.util.List;
import java.util.Optional;

/** Persistence reads require explicit trusted scope; application services obtain it from CurrentUser. */
public interface BillingRecords {
    List<InvoiceSnapshot> invoices(String tenantId);
    Optional<InvoiceSnapshot> invoice(String tenantId, String id);
    List<PaymentView> payments(String tenantId);
    Optional<PaymentView> payment(String tenantId, String id);
    List<CustomerRecord> customers(String tenantId);
    String workspaceName(String tenantId);
}
