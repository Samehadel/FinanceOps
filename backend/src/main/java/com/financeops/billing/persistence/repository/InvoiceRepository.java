package com.financeops.billing.persistence.repository;

import com.financeops.billing.persistence.entity.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    java.util.List<Invoice> findByTenantIdOrderByIssuedOnDesc(String tenantId);
    java.util.Optional<Invoice> findByIdAndTenantId(String id, String tenantId);
}
