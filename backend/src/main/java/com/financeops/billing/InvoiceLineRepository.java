package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    java.util.List<InvoiceLine> findByInvoiceIdOrderById(String invoiceId);
}
