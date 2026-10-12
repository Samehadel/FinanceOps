# Learning objective coverage

Planned coverage, not evidence of completion. Only F01 is active; later features are refined when activated.

**I — introduced:** explain the concept and identify its trust boundary or failure mode. **P — practiced:** implement it and exercise a repeatable scenario. **D — demonstrated:** record reviewed evidence against acceptance criteria across repeated success, edge, abuse, and failure cases. Demonstration applies to the exercised scope, not universal reliability. I/P/D below are target milestones; actual attainment belongs in the progress record.

| # | Objective | Introduced | Practiced | Demonstrated |
| --- | --- | --- | --- | --- |
| 1 | Structured output, schema and factual/semantic validation | F01 | F01 | F01 |
| 2 | RAG, citations, retrieval quality, versions, freshness, deletion | F01 record evidence; F02 document lifecycle | F01 citations; F02 retrieval/lifecycle | F01 record evidence; F02 full objective |
| 3 | Model/workflow evaluation, regression datasets, adversarial evaluation | F01 | F01–F04 manual replay datasets | F01 baseline; F02–F04 expanded workflows |
| 4 | Direct and indirect prompt injection | F01 direct; F02 indirect | F01–F03 | F01 direct; F02 indirect; F03 tool boundaries |
| 5 | Sensitive data, redacted logs, tenant isolation | F01 | F01–F02 | F01 record scope/logging; F02 retrieval isolation/deletion |
| 6 | Authentication, authorization, constrained agent tools | F01 access; F03 tools | F01; F03 | F01 access; F03 tool restrictions |
| 7 | Human approval, expiry, unchanged approved proposals | F04 | F04 | F04 |
| 8 | Provider rate limits, user quotas, concurrency, backpressure | F03 | F03 | F03 controlled faults; F05 deployed behavior |
| 9 | Tokens, cost attribution, budgets, runaway loops | F01 usage; F03 budgets | F01 accounting; F03 bounded execution | F01 per-call evidence; F03 full objective |
| 10 | Timeouts, bounded retries, circuit breaking, fallbacks | F01 timeout; F03 remaining controls | F01; F03 | F01 deadline; F03 controlled faults; F05 deployment |
| 11 | Idempotency, duplicate/out-of-order events, uncertain payments | F04 | F04 simulated event replay | F04 simulation; provider sandbox separately deferred |
| 12 | Durable jobs/state, cancellation, crash recovery, concurrency | F03 bounded investigation/cancellation; F04 durability | F03; F04 | F04 recovery/races; F05 deployed recovery |
| 13 | Observability: retrieval, model, tools, financial operations | F01 request/model; F02–F04 added spans | F01–F04 | F01 baseline; F04 end-to-end; F05 alerts |
| 14 | Secrets, deployment, IaC, CI/CD, rollback, alerts | F01 server-side secrets; F05 operations | F01 secrets; F05 remaining controls | F01 secret/log checks; F05 deployment/rollback/alerts |
| 15 | UX: uncertainty, partial results, failures, escalation | F01 uncertainty/failure; F03 partial/escalation | F01; F03–F04 | F01 baseline; F03–F04 workflow UX |
| 16 | Additional concepts driven by concrete needs | At the active feature's justified need | Same feature | Required scenario evidence in that feature |

Manual evaluation and recorded replay remain the approach. CI/CD in F05 covers build, deployment and rollback; it does not add test automation to the learning scope. Simulations demonstrate application controls; they do not demonstrate real payment-provider semantics or real-provider reliability.

## Actual coverage at the October 12, 2026 advisory review

The F01 specification introduces grounding, trust boundaries, structured output, and bounded failure. Reading or understanding by the learner has not yet been demonstrated. Foundation source supports implementation of backend identity scope and exact calculations. Prior manual checks are documented, but were not rerun in this review. No AI objective is marked demonstrated by this review.
