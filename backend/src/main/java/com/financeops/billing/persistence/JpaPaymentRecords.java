package com.financeops.billing.persistence;

import com.financeops.billing.application.port.PaymentRecords;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import com.financeops.billing.persistence.repository.PaymentRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaPaymentRecords implements PaymentRecords {
    private final PaymentRepository payments;
    private final PaymentFactsMapper facts;

    JpaPaymentRecords(PaymentRepository payments, PaymentFactsMapper facts) {
        this.payments = payments;
        this.facts = facts;
    }

    @Override
    public List<PaymentView> payments(String tenantId) {
        return payments.findByTenantIdOrderByRecordedOnDesc(tenantId).stream().map(facts::map).toList();
    }

    @Override
    public Optional<PaymentView> payment(String tenantId, String id) {
        return payments.findByIdAndTenantId(id, tenantId).map(facts::map);
    }
}
