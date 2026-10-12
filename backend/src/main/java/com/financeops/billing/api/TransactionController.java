package com.financeops.billing.api;

import com.financeops.billing.contract.BillingQueries;
import com.financeops.billing.api.dto.BillingResponses.PaymentView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final BillingQueries service;

    public TransactionController(BillingQueries service) {
        this.service = service;
    }

    @GetMapping
    public List<PaymentView> all() {
        return service.transactions().stream().map(BillingResponseMapper::payment).toList();
    }

    @GetMapping("/{id}")
    public PaymentView find(@PathVariable String id) {
        return BillingResponseMapper.payment(service.transaction(id));
    }
}
