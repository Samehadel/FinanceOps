package com.financeops.billing.application.port;

import com.financeops.billing.domain.CustomerRecord;
import java.util.List;

/** Tenant-scoped customer reads. Callers obtain the trusted tenant from CurrentUser. */
public interface CustomerRecords {
    List<CustomerRecord> customers(String tenantId);
}
