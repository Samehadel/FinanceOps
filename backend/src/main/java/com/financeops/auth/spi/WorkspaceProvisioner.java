package com.financeops.auth.spi;

/** Signup's synchronous extension: the implementation joins the caller's transaction. */
public interface WorkspaceProvisioner {
    void provision(String tenantId, String ownerName, String recordSuffix);
}
