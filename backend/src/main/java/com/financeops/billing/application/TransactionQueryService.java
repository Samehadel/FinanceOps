package com.financeops.billing.application;

import com.financeops.auth.contract.CurrentUser;
import com.financeops.billing.application.port.BillingRecords;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.shared.domain.RecordNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransactionQueryService {
    private final CurrentUser access;
    private final BillingRecords records;

    public TransactionQueryService(CurrentUser access, BillingRecords records) {
        this.access = access;
        this.records = records;
    }

    public List<PaymentView> all() {
        return records.payments(access.currentTenant());
    }

    public PaymentView find(String id) {
        return records.payment(access.currentTenant(), id)
            .orElseThrow(() -> new RecordNotFoundException("No accessible matching record."));
    }
}
