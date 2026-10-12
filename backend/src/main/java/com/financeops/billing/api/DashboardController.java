package com.financeops.billing.api;

import com.financeops.billing.contract.BillingQueries;
import com.financeops.billing.api.dto.BillingResponses.Dashboard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final BillingQueries service;

    public DashboardController(BillingQueries service) {
        this.service = service;
    }

    @GetMapping("/api/dashboard")
    public Dashboard summary() {
        return BillingResponseMapper.dashboard(service.dashboard());
    }
}
