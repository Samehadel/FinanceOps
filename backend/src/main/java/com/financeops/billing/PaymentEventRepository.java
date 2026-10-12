package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentEventRepository extends JpaRepository<PaymentEvent, String> {
    java.util.List<PaymentEvent> findByPaymentIdAndTenantIdOrderByOccurredOn(String paymentId, String tenantId);
}
