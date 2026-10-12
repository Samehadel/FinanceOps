package com.financeops.billing.application;

import com.financeops.auth.contract.CurrentUser;
import com.financeops.billing.application.port.BillingRecords;
import com.financeops.billing.domain.InvoiceCalculator;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import com.financeops.shared.domain.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/** Internal invoice use case behind BillingQueries; callers supply record IDs, never tenant IDs. */
@Service
@Transactional(readOnly = true)
public class InvoiceQueryService {
    private final CurrentUser access;
    private final BillingRecords records;
    private final LocalDate referenceDate;

    public InvoiceQueryService(CurrentUser access, BillingRecords records,
                               @Value("${financeops.reference-date}") LocalDate referenceDate) {
        this.access = access;
        this.records = records;
        this.referenceDate = referenceDate;
    }

    public List<InvoiceView> all() {
        return records.invoices(access.currentTenant()).stream()
            .map(invoice -> InvoiceCalculator.calculate(invoice, referenceDate)).toList();
    }

    public InvoiceView find(String id) {
        return records.invoice(access.currentTenant(), id)
            .map(invoice -> InvoiceCalculator.calculate(invoice, referenceDate))
            .orElseThrow(() -> new RecordNotFoundException("No accessible matching record."));
    }
}
