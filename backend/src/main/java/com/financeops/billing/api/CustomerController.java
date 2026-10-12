package com.financeops.billing.api;

import com.financeops.billing.contract.BillingQueries;
import com.financeops.billing.api.dto.BillingResponses.CustomerView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final BillingQueries service;

    public CustomerController(BillingQueries service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomerView> all() {
        return service.customers().stream().map(BillingResponseMapper::customer).toList();
    }

}
