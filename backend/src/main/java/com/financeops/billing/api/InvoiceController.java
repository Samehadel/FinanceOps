package com.financeops.billing.api;

import com.financeops.billing.contract.BillingQueries;
import com.financeops.billing.api.dto.BillingResponses.InvoiceView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final BillingQueries service;

    public InvoiceController(BillingQueries service) {
        this.service = service;
    }

    @GetMapping
    public List<InvoiceView> all() {
        return service.invoices().stream().map(BillingResponseMapper::invoice).toList();
    }

    @GetMapping("/{id}")
    public InvoiceView find(@PathVariable String id) {
        return BillingResponseMapper.invoice(service.invoice(id));
    }
}
