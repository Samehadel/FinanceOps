package com.financeops.billing.application;

import com.financeops.auth.contract.CurrentUser;
import com.financeops.billing.application.port.BillingRecords;
import com.financeops.billing.contract.BillingFacts.Dashboard;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
public class DashboardQueryService {
    private final CurrentUser access;
    private final BillingRecords records;
    private final InvoiceQueryService invoices;
    private final LocalDate referenceDate;

    public DashboardQueryService(CurrentUser access, BillingRecords records, InvoiceQueryService invoices,
                                 @Value("${financeops.reference-date}") LocalDate referenceDate) {
        this.access = access;
        this.records = records;
        this.invoices = invoices;
        this.referenceDate = referenceDate;
    }

    public Dashboard summary() {
        String tenantId = access.currentTenant();
        List<InvoiceView> authorized = invoices.all();
        BigDecimal overdue = authorized.stream().filter(InvoiceView::overdue)
            .map(InvoiceView::outstanding).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dashboard(records.workspaceName(tenantId), referenceDate, sum(authorized, InvoiceView::total),
            sum(authorized, InvoiceView::settled), sum(authorized, InvoiceView::pending),
            sum(authorized, InvoiceView::outstanding), overdue, authorized.size(), records.customers(tenantId).size());
    }

    private BigDecimal sum(List<InvoiceView> records, Function<InvoiceView, BigDecimal> value) {
        return records.stream().map(value).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
