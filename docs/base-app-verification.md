# Non-AI foundation: manual verification

Run date: October 12, 2026. Runtime: JDK 25, Spring Boot 4.1.1, PostgreSQL 18.6, Angular 22.2.2. These scenarios verify the base app, not F01's AI acceptance criteria.

The table below records the original foundation checks before the dataset expansion. For current counts, balances, and additional scenarios, see [Expanded practice dataset](practice-dataset.md). Start with reset fixtures using the README instructions. Use Maya and Omar in separate browser profiles or isolated cookie jars; tabs in one browser profile share the same session. Read-only financial actions leave the seeded financial rows unchanged. Signup intentionally adds records only to its new workspace.

| Scenario | Steps | Expected / observed result |
| --- | --- | --- |
| Authentication | Open `/api/dashboard` without a session; sign in as Maya; inspect account | Anonymous API returns 401; login returns 204; profile identifies Maya/T-A/SUPPORT |
| CSRF | POST login without X-XSRF-TOKEN, then with the raw CSRF cookie | 403 without token; login succeeds with cookie/header |
| Exact balance | Open INV-A101 | $120 total, $70 settled, $50 pending, $50 outstanding, OPEN, not overdue |
| Invoice evidence | Inspect INV-A101 lines and associated payments | $100 subscription plus 2 × $10 seats; PAY-A201 settled October 5; PAY-A202 pending with unknown settlement |
| Dashboard | Inspect Maya's overview | $700 invoiced, $170 settled, $50 pending, $530 outstanding, $240 overdue; 4 invoices, 2 customers |
| Other statuses | Inspect INV-A102, INV-A103, INV-A104 | A102 overdue $240; A103 paid $0; failed A204 does not reduce A104's $240 balance |
| Isolation | Maya requests INV-B101 directly and lists `/api/invoices?tenantId=T-B` | Direct read returns generic 404; supplied tenant does not change list scope |
| Missing record | Maya requests INV-A999 | Same generic 404 as unauthorized record |
| Independent identity | Sign in as Omar in an isolated session | Only INV-B101, $900 outstanding; PAY-A201 direct access returns generic 404 |
| Read-only finance | Authenticated PATCH `/api/invoices/INV-A101` with valid CSRF | 405 Method Not Allowed; no financial mutation |
| Logout | Sign out, then request `/api/auth/me` | 204 logout, then 401; browser returns to login |
| Signup | Create a learner through the Angular form | Automatic login; new isolated workspace; $120 invoice, $70 settled, $50 balance |
| Signup authorization | Include `tenantId: T-A` and `role: ADMIN` in signup JSON | Backend creates its own tenant, always SUPPORT; demo invoices inaccessible |
| Validation | Invalid email/short password, then duplicate email differing in case | 400 validation; normalized duplicate returns 409 without creating another workspace |
| UI filtering | Filter transactions to PENDING and expand history | Only PAY-A202; EV-A302 shown; settlement is unknown |
| Responsive layout | Inspect overview and invoice at desktop, transactions at 390 × 844 | Responsive navigation, mobile account access, stacked content, tables scroll within cards |

Observed via live browser interactions and direct API calls. Both source builds passed. Both container builds and the final three-service Compose startup passed. Proxied login and invoice reads passed; maximum-length names were accepted and oversized UTF-8 passwords were rejected. Temporary verification accounts were removed; the demo fixtures and independently created user accounts were preserved. Details are recorded in the progress log. No real model behavior has been evaluated; AI criteria remain unverified.
