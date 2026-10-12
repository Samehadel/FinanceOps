package com.financeops.billing.application.port;

import com.financeops.billing.domain.InvoiceSnapshot;
import java.util.List;
import java.util.Optional;

/** Tenant-scoped invoice reads. Callers obtain the trusted tenant from CurrentUser. */
public interface InvoiceRecords {
    List<InvoiceSnapshot> invoices(String tenantId);
    Optional<InvoiceSnapshot> invoice(String tenantId, String id);
}
