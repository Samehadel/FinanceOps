package com.financeops.billing;

import org.springframework.data.jpa.repository.JpaRepository;

interface TenantRepository extends JpaRepository<Tenant, String> {

}
