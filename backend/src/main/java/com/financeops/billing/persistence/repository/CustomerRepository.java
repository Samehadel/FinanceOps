package com.financeops.billing.persistence.repository;

import com.financeops.billing.persistence.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    java.util.List<Customer> findByTenantIdOrderByName(String tenantId);
    java.util.Optional<Customer> findByIdAndTenantId(String id, String tenantId);
}
