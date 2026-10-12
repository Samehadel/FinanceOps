package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerRepository extends JpaRepository<Customer, String> {
    java.util.List<Customer> findByTenantIdOrderByName(String tenantId);
    java.util.Optional<Customer> findByIdAndTenantId(String id, String tenantId);
}
