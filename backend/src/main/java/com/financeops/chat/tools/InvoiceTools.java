package com.financeops.chat.tools;

import com.financeops.billing.contract.BillingQueries;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceTools {
    private final BillingQueries billingQueries;

    @Tool(name = "fetchInvoiceById", description = "Fetch specific invoice by invoice ID, returning only one record, or RecordNotFoundException if not exist")
    public String fetchInvoiceById(@ToolParam(description = "Invoice ID") String id, ToolContext context) {
        if (!(context.getContext().get(SensitiveFieldMasker.TOOL_CONTEXT_KEY) instanceof SensitiveFieldMasker masker)) {
            throw new IllegalStateException("Sensitive field masking context is required");
        }
        log.info("Fetching invoice by ID: {} using InvoiceTools.fetchInvoiceById.", id);
        return masker.maskResult(billingQueries.invoice(id));
    }

    /** Register tenant-owned values before masking user text and restored conversation history. */
    public void registerSensitiveValues(SensitiveFieldMasker masker) {
        billingQueries.customers().forEach(customer -> {
            masker.register(InvoiceView.Fields.customerId, customer.id());
            masker.register(InvoiceView.Fields.customerName, customer.name());
            masker.register(InvoiceView.Fields.customerEmail, customer.email());
        });
    }
}
