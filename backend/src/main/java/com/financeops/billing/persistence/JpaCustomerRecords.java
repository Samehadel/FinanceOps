package com.financeops.billing.persistence;

import com.financeops.billing.application.port.CustomerRecords;
import com.financeops.billing.domain.CustomerRecord;
import com.financeops.billing.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JpaCustomerRecords implements CustomerRecords {
    private final CustomerRepository customers;

    public JpaCustomerRecords(CustomerRepository customers) {
        this.customers = customers;
    }

    @Override
    public List<CustomerRecord> customers(String tenantId) {
        return customers.findByTenantIdOrderByName(tenantId).stream()
            .map(customer -> new CustomerRecord(customer.getId(), customer.getName(), customer.getEmail())).toList();
    }
}
