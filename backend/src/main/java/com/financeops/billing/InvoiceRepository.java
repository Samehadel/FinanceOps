package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface InvoiceRepository extends JpaRepository<Invoice, String> {
    java.util.List<Invoice> findByTenantIdOrderByIssuedOnDesc(String tenantId);
    java.util.Optional<Invoice> findByIdAndTenantId(String id, String tenantId);
}
