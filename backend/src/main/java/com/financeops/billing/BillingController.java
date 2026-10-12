package com.financeops.billing;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.financeops.billing.BillingViews.*;

@RestController
@RequestMapping("/api")
public class BillingController {
    private final BillingService billing;

    public BillingController(BillingService billing) {
        this.billing = billing;
    }

    @GetMapping("/dashboard")
    public Dashboard dashboard(Authentication auth) {
        return billing.dashboard(auth.getName());
    }

    @GetMapping("/customers")
    public List<CustomerView> customers(Authentication auth) {
        return billing.customers(auth.getName());
    }

    @GetMapping("/invoices")
    public List<InvoiceView> invoices(Authentication auth) {
        return billing.invoices(auth.getName());
    }

    @GetMapping("/invoices/{id}")
    public InvoiceView invoice(Authentication auth, @PathVariable String id) {
        return billing.invoice(auth.getName(), id);
    }

    @GetMapping("/transactions")
    public List<PaymentView> transactions(Authentication auth) {
        return billing.transactions(auth.getName());
    }

    @GetMapping("/transactions/{id}")
    public PaymentView transaction(Authentication auth, @PathVariable String id) {
        return billing.transaction(auth.getName(), id);
    }

    @GetMapping("/health")
    public java.util.Map<String, String> health() {
        return java.util.Map.of("status", "UP");
    }
}
