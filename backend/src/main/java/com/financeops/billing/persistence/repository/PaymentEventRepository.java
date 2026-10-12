package com.financeops.billing.persistence.repository;

import com.financeops.billing.persistence.entity.PaymentEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, String> {
    java.util.List<PaymentEvent> findByPaymentIdAndTenantIdOrderByOccurredOn(String paymentId, String tenantId);
}
