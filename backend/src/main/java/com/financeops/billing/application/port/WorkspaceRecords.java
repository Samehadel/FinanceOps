package com.financeops.billing.application.port;

/** Tenant-scoped workspace reads. Callers obtain the trusted tenant from CurrentUser. */
public interface WorkspaceRecords {
    String workspaceName(String tenantId);
}
