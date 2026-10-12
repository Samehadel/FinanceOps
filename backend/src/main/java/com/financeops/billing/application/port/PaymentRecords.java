package com.financeops.billing.application.port;

import com.financeops.billing.contract.BillingFacts.PaymentView;
import java.util.List;
import java.util.Optional;

/** Tenant-scoped payment reads. Callers obtain the trusted tenant from CurrentUser. */
public interface PaymentRecords {
    List<PaymentView> payments(String tenantId);
    Optional<PaymentView> payment(String tenantId, String id);
}
