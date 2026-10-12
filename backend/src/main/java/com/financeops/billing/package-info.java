/** Workspace ownership, financial rules, persistence, and read-only billing queries. */
@org.springframework.modulith.ApplicationModule(
    displayName = "Billing",
    allowedDependencies = {"auth :: identity", "auth :: provisioning", "shared :: errors"}
)
package com.financeops.billing;
