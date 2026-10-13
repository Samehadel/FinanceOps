# FinanceOps Desk — Progress record

## Completed features

**Non-AI application foundation — implemented October 12, 2026.**

- Spring Boot 4.1.1 / Spring Security sessions, BCrypt passwords, login, signup, logout, and current-user profile.
- PostgreSQL 18.6, Flyway schema/fixtures, two isolated demo identities, and independent signup workspaces.
- Angular 22.2.2 overview, invoices/detail, transactions/events, customers, account, search/status filters, and responsive layouts.
- Exact backend balances and read-only financial endpoints; no AI configuration required.
- Setup and reset instructions: [Application README](../README.md).
- Repeatable manual scenarios: [Base app verification](base-app-verification.md).

This foundation is distinct from completion of F01's chat and model criteria.

## Backend modular monolith — October 12, 2026

- Organized the backend into closed authentication, billing, and shared modules with explicit Spring Modulith 2.1.1 dependency declarations. HTTP adapters, application use cases, domain calculations, and persistence implementations are separate inside each feature.
- Exposed trusted current-user identity, a synchronous workspace-provisioning SPI, and an immutable tenant-scoped billing query facade. Other modules cannot import internal repositories, entities, controllers, or services. Future features should use the [documented module contracts](backend-architecture.md).
- Clean Maven packaging and the source Docker build passed. Both ran the static module/layer checks despite `-DskipTests`. A temporary billing reference to an authentication repository correctly failed the build; the probe was removed and the clean build passed again. The runtime JAR contains Modulith API metadata, without ArchUnit, Modulith core, or the verification class.
- Compared 75 live API responses before and after the refactor across Maya and Omar: profiles, dashboards, customer/invoice/transaction lists, and every invoice and transaction detail matched exactly. Recomputed all demo invoice balances with exact decimal arithmetic.
- Rechecked anonymous access, wrong passwords, login/logout, missing CSRF, generic missing/cross-tenant 404s, ignored tenant parameters, rejected financial PATCH requests, and the due-today boundary.
- Signup verified maximum-length names, oversized UTF-8 password rejection, server-owned role/tenant selection, normalized duplicate rejection, and complete expanded fixtures: 8 customers, 11 invoices, 19 payments, $3396.48 outstanding.
- Concurrent signup with the same normalized email produced one 201 and one 409. The database uniqueness failure rolled back the second workspace; exactly one complete workspace remained for that account.
- Removed only the two temporary verification accounts. Row hashes for all seven application tables matched the baseline exactly, including existing user accounts. Flyway validated all four migrations with their original checksums; no migration or re-seeding was needed. The modular backend is running through the existing Angular/Nginx proxy.
- Angular source and API URLs/JSON fields are unchanged. This increment adds build-time structural validation, not a behavioral automated test suite. F01's AI requirements remain outstanding.

## Current feature

**F01 — Read-only billing chat**

- Requirements: [F01 specification](features/F01-read-only-billing-chat.md)
- Scenarios: F01-S01 through F01-S09
- Status: foundation and text-only chat present in source; record-grounded chat and full AI acceptance evaluation remain outstanding
- Current task: [F01-T01 — Trusted invoice explanation](features/F01-task-01-grounded-invoice.md); design proposal pending. Scenario focus: S01, S03–S06, S09, and F01-T01-S01 (forged client history).

## Open decisions

- Source now contains OpenAI configuration and a default model setting; effective runtime model and successful provider behavior remain unverified in this review.
- Dependencies are pinned for the base app; JDK 25 is selected.
- Authentication is implemented using Spring Security sessions and server-owned tenant membership.
- Authorized data retrieval is implemented; model context construction remains undecided.
- Response contract and factual/evidence validation strategy.
- Controlled stub mechanism and timeout handling.
- Whether model tools are needed for F01.

## Outstanding acceptance criteria

F01-AC01 and F01-AC02 have supporting base-app evidence for authenticated tenant scope and exact settled-only calculations. Prompt-based controls and the complete F01 journey remain unverified. F01-AC03 through F01-AC08 require future AI implementation and evaluation.

## Evidence log

| Date | Scenario / criterion | Evidence reference | Demonstrated outcome | Gaps |
| --- | --- | --- | --- | --- |
| 2026-10-12 | Base builds and manual flows | [Base app verification](base-app-verification.md) | Maven package and Angular production build passed; browser login/signup/logout and desktop/mobile billing screens exercised | Automated tests deliberately outside scope |
| 2026-10-12 | Identity and financial scope | [Base app verification](base-app-verification.md) | 401 anonymous, 403 missing CSRF, generic 404 cross-tenant/missing; tenant parameter ignored; financial PATCH rejected with 405 | No prompt/model handling yet |
| 2026-10-12 | Docker startup | Compose runtime and proxied API | All three services running; PostgreSQL healthy; `/api/health` UP through Angular/Nginx; proxied login 204 and INV-A101 balance $50 | Local fictional setup only |
| 2026-10-12 | Signup edge cases | Live container API | 100-character name creates workspace successfully; oversized UTF-8 password rejected with 400; wrong password returns 401; temporary verification accounts removed | No automated regression suite |
| 2026-10-12 | Fixture integrity | Database hash before/after manual reads and security scenarios | Seeded financial rows hash remained `1f41a1a2d2139e05cb5e73bec382c769`; all stored password hashes verified as BCrypt | Hash excludes independent signup workspaces |
| 2026-10-12 | Financial facts | INV-A101 and INV-B101 API/browser results | A101: $120 total, $70 settled, $50 pending, $50 outstanding, not overdue; B101: $900 outstanding | Provider/payment settlement reliability not evaluated |

Update this record after reviews. Separate observed results from design intentions and assumptions. Do not claim checks passed without credible supplied evidence or actual execution.

## Advisory review — October 12, 2026

- Read the roadmap, F01 specification, progress and foundation verification documents; inspected BillingController, BillingService and SecurityConfig. Source supports authenticated tenant-scoped reads, exact BigDecimal balances, and settled-only payment subtraction. Runtime/build/security checks were not rerun. Earlier outcomes remain documented evidence, not newly observed results.
- F01 remains active and incomplete. The roadmap's “not implemented or verified” status refers to the AI feature, not the existing foundation. No additional feature is marked complete.
- [Coverage map](learning-coverage.md) assigns all 16 objectives to introduction, practice and demonstration milestones. These are planned targets. Learner understanding and AI demonstrations remain unverified.
- Next increment: engineer proposes the identity → permitted facts → model → validation → UI approach, then implements S01 with S06/S09 rejection evidence. S09 adds a valid-schema, authorized-citation, incorrect-balance case for AC03/AC06.
- Open decisions remain provider/model, minimal model context, response contract, factual validation, controlled stubs and deadline handling. No provider/version recommendation was made in this review.
- Outstanding: complete AC01–AC08 across S01–S09; repeated real-model runs, redacted evidence, financial immutability, latency and usage/cost records. Existing evaluation plan still applies; include S09 as a controlled-stub run.
- Learning pack resources were opened in official Spring AI and OWASP documentation. Next learner evidence: explain the settled-versus-pending contradiction and propose where the backend rejects it.
- Later-feature specifications, test automation and payment sandbox behavior remain deferred. No application code was generated or changed by this review.

## First task assignment — October 13, 2026

- Inspected current ChatService, Angular chat component, billing contracts and logging advisor. Text-only chat and model integration exist in source; chat does not retrieve billing facts and returns unvalidated plain text. No runtime/model checks were run.
- Assigned F01-T01, reusing the existing chat and tenant-scoped query foundation. Engineer proposes the approach before implementation. No application code was changed.
- Identified a concrete AC08 gap: routine INFO logs currently include full model request/response content. Content exclusion is required within this task before billing grounding is demonstrated.
- Learning coverage target: practice objectives 1, 3, 4, 5, 6 and 15, record evidence/citations for objective 2 and diagnostics for 9/13. No new demonstrated learning coverage is claimed.
- Remaining decisions: invoice selection, minimal context, response contract, semantic/evidence checks, controlled responses and failure UX. Task evidence and full F01 operational/deadline/isolation evaluation are outstanding.

## Deferred work

- Test automation.
- Payment provider integration and sandbox verification.
- Policy RAG, document lifecycle, and indirect injection exercises.
- Autonomous investigation tools and execution budgets.
- Refund proposals, approvals, and simulated execution.
- Durable workflow state and operational recovery.
- Cloud deployment, infrastructure as code, CI/CD, rollback, and alerts.

## Expanded practice data — October 12, 2026

- Added seven customers, ten invoices, eighteen payments, and thirty-one events to each existing workspace using an additive V4 migration. Existing financial records and user accounts remain intact.
- Future signups receive the same expansion through the shared fixture loader. The migration records a checksum of the fixture resource.
- Manual API checks verified all invoice calculations for Maya and Omar, original A101/B101 balances, the due-today boundary, cross-tenant denial, and the complete new-signup dataset. Database counts verified exact additions for every existing workspace, including the user's workspace.
- Source packaging and container runtime passed. Restart verified that migrations do not duplicate records. The temporary verification account was removed. Docker's jar path accommodates the locally renamed Maven artifact.
- [Dataset reference and expected balances](practice-dataset.md). Earlier foundation counts in this log describe the pre-expansion fixtures.
