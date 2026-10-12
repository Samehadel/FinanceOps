# Product context and roadmap

## Learner context

- Senior software engineer; little to moderate experience with LLM APIs and cloud deployment.
- Chosen stack: Spring Boot, Spring Data, Spring AI, PostgreSQL, and Angular.
- An LLM provider account and a cloud account are available. Provider/model and cloud names remain unspecified.
- No payment provider sandbox yet. Use local fictional payment records initially.
- All learning areas matter equally, with AI reliability and security first.
- Start with a working backend, dummy data, and a simple user-facing chat.
- Skip test automation for now; assess outcomes through repeatable manual scenarios and evidence.

## Users

| Role | Responsibilities |
| --- | --- |
| Support employee | View permitted billing records and open investigations |
| Finance analyst | Investigate discrepancies and propose refunds |
| Finance approver | Approve or reject proposals within authority |
| Administrator | Manage access, policies, quotas, and configuration |

For F01, “the user's data” means records the authenticated employee is authorized to access. The application remains an internal finance platform.

## Non-negotiable boundaries

- Backend code owns financial calculations, authorization, business rules, and ledger state.
- The LLM may interpret, explain, investigate, and propose actions. Its output cannot grant permission or bypass backend controls.
- Use fictional data and local simulations or sandboxes exclusively.
- Give one active feature at a time. Add components only when a requirement justifies them.
- Keep solution design with the engineer; offer graduated hints when requested.
- Distinguish demonstrated behavior, assumptions, and unverified claims during review.

## Roadmap

| Feature | Intended outcome | Status |
| --- | --- | --- |
| F01 | Read-only billing chat with trustworthy answers | Active; not implemented or verified |
| F02 | Billing policy retrieval: citations, versions, freshness, deletion, indirect injection | Deferred |
| F03 | Constrained investigation tools: budgets, failures, observability | Deferred |
| F04 | Refund proposals and approval controls using local simulated execution | Deferred |
| F05 | Cloud deployment and operational resilience | Deferred |

Later scenarios introduce quotas, rate limits, concurrency, cost attribution, bounded retries, circuit breaking, idempotency, uncertain outcomes, durable jobs, recovery, secrets, infrastructure as code, CI/CD, rollback, and alerts when the active workflow creates a need.

## Dependency baseline

Use the latest compatible stable releases, pin exact versions when initializing, and recheck documentation before selecting provider-specific API behavior. Do not use preview releases solely to obtain a higher version number.

The planning lookup on October 12, 2026 identified these compatible lines:

| Component | Planning baseline |
| --- | --- |
| Spring Boot | Latest stable 4.1.x patch |
| Spring AI | 2.0.1; documented support for Boot 4.0.x and 4.1.x |
| Spring Data | Managed by Spring Boot dependency management |
| Angular and CLI | Latest stable 22.0.x patch |
| Node.js / TypeScript | Node 24.15+ within 24.x and TypeScript 6.0.x fit the documented Angular 22 range |
| PostgreSQL | Latest stable supported release at initialization |

This is a planning baseline, not a verified build or committed dependency lock.

Official sources:

- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Spring AI compatibility and dependency management](https://docs.spring.io/spring-ai/reference/getting-started.html)
- [Angular version compatibility](https://angular.dev/reference/versions)
- [PostgreSQL version policy](https://www.postgresql.org/support/versioning/)
