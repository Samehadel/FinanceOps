package com.financeops.auth.contract;

/** Trusted identity of the current session. Callers cannot choose an email, tenant, or role. */
public interface CurrentUser {
    String currentTenant();
    UserProfile profile();
}
