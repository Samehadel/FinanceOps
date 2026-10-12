# FinanceOps Desk — Product and learning guide

FinanceOps Desk is a practice finance operations application for a fictional SaaS company. It supports billing explanations, payment investigations, reconciliation, and eventually controlled refunds.

The non-AI foundation is now implemented. Start with the [application setup guide](../README.md) to run login/signup and the billing workspace. See [base-app verification](base-app-verification.md) for practice scenarios and [backend architecture](backend-architecture.md) for module boundaries and extension contracts. The AI feature remains incomplete.

## How to use these documents

1. Read [Product context and roadmap](product-roadmap.md).
2. Work only on the active feature: [F01 — Read-only billing chat](features/F01-read-only-billing-chat.md).
3. Complete its learning pack, propose your design, then implement it.
4. Demonstrate the acceptance criteria and scenarios; record evidence in [Progress](progress.md).

Track introduced, practiced, and demonstrated objectives in the [learning coverage map](learning-coverage.md).

The engineer owns UI, backend, integrations, and deployment. The product owner/scenario designer supplies requirements, challenges design assumptions, and reviews evidence. Do not implement the application on the engineer's behalf unless explicitly asked.

Requirements describe required outcomes. Design choices remain the engineer's responsibility. Do not mark a feature complete based on assumptions or a single successful demonstration.

All records are fictional. Use local simulations or provider sandboxes; never require real customer data or real money movements. Test automation is outside the current learning scope; reproducible manual evaluation remains required.
