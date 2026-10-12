package com.financeops.billing.persistence;

import com.financeops.billing.application.port.WorkspaceRecords;
import com.financeops.billing.persistence.repository.TenantRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaWorkspaceRecords implements WorkspaceRecords {
    private final TenantRepository tenants;

    public JpaWorkspaceRecords(TenantRepository tenants) {
        this.tenants = tenants;
    }

    @Override
    public String workspaceName(String tenantId) {
        return tenants.findById(tenantId).orElseThrow().getName();
    }
}
