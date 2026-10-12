package com.financeops.billing.persistence.repository;

import com.financeops.billing.persistence.entity.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {

}
