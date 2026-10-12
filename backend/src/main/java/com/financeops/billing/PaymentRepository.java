package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentRepository extends JpaRepository<Payment, String> {
    java.util.List<Payment> findByTenantIdOrderByRecordedOnDesc(String tenantId);
    java.util.List<Payment> findByInvoiceIdAndTenantIdOrderByRecordedOnDesc(String invoiceId, String tenantId);
    java.util.Optional<Payment> findByIdAndTenantId(String id, String tenantId);
}
