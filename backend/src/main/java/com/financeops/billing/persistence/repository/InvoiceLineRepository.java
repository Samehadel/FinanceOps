package com.financeops.billing.persistence.repository;

import com.financeops.billing.persistence.entity.InvoiceLine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    @Query("""
        select line from InvoiceLine line, Invoice invoice
        where line.invoiceId = invoice.id and invoice.id = :invoiceId
          and invoice.tenantId = :tenantId
        order by line.id
        """)
    java.util.List<InvoiceLine> findScopedLines(String invoiceId, String tenantId);
}
