# FinanceOps Desk

A local learning app built with Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, and Angular. Billing works without an API key; the optional AI assistant uses OpenAI.

## Run with Docker

From this directory, with Docker Desktop running:

```sh
docker compose up --build -d
```

Open **http://localhost:4200**. The first build downloads dependencies and images. PostgreSQL health gates backend startup; allow a few seconds for migrations and account creation before signing in. Watch startup with `docker compose logs -f backend`.

| Account | Email | Default password | Workspace |
| --- | --- | --- | --- |
| Maya | maya@financeops.local | Practice123! | Cedar (T-A) |
| Omar | omar@financeops.local | Practice123! | Harbor (T-B) |

These are fictional local practice credentials. Signup creates a separate workspace with a starter $120 invoice and $70 settled payment, plus the [expanded practice dataset](docs/practice-dataset.md) of seven additional customers, ten invoices, and eighteen payments. It cannot enroll an account into an existing tenant or choose an elevated role.

`docker compose stop` stops the application and preserves data. `docker compose down` removes its containers and preserves the named database volume.

### Reset practice data

This deletes this project's local database, including accounts created through signup:

```sh
docker compose down -v
docker compose up --build -d
```

Flyway recreates the schema and fixtures; the backend creates Maya and Omar with BCrypt passwords. Financial read endpoints never modify those fixtures. Existing demo passwords are not overwritten on restart.

## Run during development

Requirements: JDK 25, Maven 3.9.12+, Node 22.22.3+ within 22.x or Node 24.15+ within 24.x (Angular's supported ranges), and Docker.

```sh
docker compose up -d db
mvn -f backend/pom.xml spring-boot:run
```

In a second terminal:

```sh
cd frontend
npm ci
npm start
```

Open **http://127.0.0.1:4200**. Angular proxies `/api` to the backend at port 8080. In the Docker setup, Nginx provides the same proxy and supports direct Angular route navigation. Do not mix localhost and 127.0.0.1 during a session: cookies are host-specific.

PostgreSQL is available at `localhost:5433`, database/user `financeops`, local password `local-practice-only`. It is bound to loopback. Use your database client to inspect tables or practice queries. See `.env.example` for optional local overrides. When running the backend directly, its environment variables must be exported in that terminal; Compose reads `.env` for its containers. Set `SESSION_COOKIE_SECURE=true` if later serving the backend over HTTPS.

Build checks:

```sh
mvn -f backend/pom.xml package -DskipTests
npm --prefix frontend run build
npm --prefix frontend run format
```

No automated test suite is included, following the learning scope. Repeatable manual scenarios and observed results are in [docs/base-app-verification.md](docs/base-app-verification.md).

## What you can practice

- Login, signup, logout, session restoration, and profile inspection.
- Dashboard: invoiced, settled, pending, outstanding, and overdue totals.
- Invoice search/status filters, line items, due dates, and payment evidence.
- Transactions: settled, pending, failed, recorded settlement dates, and event history.
- Customer directory and per-customer outstanding totals.
- Tenant isolation between Maya, Omar, and newly created accounts.
- Text-only conversations with the AI assistant, opened from the sparkle icon in the main header.

### Configure AI chat

Set `OPENAI_API_KEY` in the backend environment and restart the backend. For Docker, add it to the ignored `.env` file and run `docker compose up --build -d`. For a direct Maven launch, export it in that terminal before `mvn -f backend/pom.xml spring-boot:run`. The key is never sent to the frontend.

`OPENAI_MODEL` defaults to `gpt-5.4-nano`, an inexpensive model with function calling support ([official model details](https://developers.openai.com/api/docs/models/gpt-5.4-nano)). Chat uses the official OpenAI Java SDK with low reasoning effort, a 2,048-token completion limit, a 45-second timeout, and no automatic retries. No tools are registered or called. Each submission makes one direct Chat Completions call with storage disabled.

The assistant explains billing concepts and details supplied in the conversation. It cannot retrieve workspace records or execute financial actions. Conversations live only in the page and clear on navigation or reload. The latest ten complete turns accompany each question; requests are limited to 21 messages, 8,000 characters per message, and 48,000 characters total. The UI limits each question to 4,000 characters. Missing configuration and provider failures return a recoverable 503 without exposing provider details.

All amounts are USD. Only **SETTLED** payments reduce balances. Backend calculations use `BigDecimal` and PostgreSQL `numeric(14,2)`. The reference date is fixed at **2026-10-12**, matching the F01 fixtures. A due date equal to the reference date is not overdue. Missing settlement dates remain unknown.

Maya has extra practice records: an overdue invoice, a paid invoice, and a failed payment. These original records contribute $700 invoiced, $170 settled, $50 pending, $530 outstanding, and $240 overdue; the expanded dataset adds more records and balances. INV-A101 keeps the original expected $50 balance. Omar's INV-B101 has $900 outstanding and no payments. All workspaces now include the [expanded dataset and practice scenarios](docs/practice-dataset.md); Maya has 9 customers/14 invoices/22 payments, and Omar has 8 customers/11 invoices/18 payments.

## Structure and API

- `backend/`: a [modular monolith](docs/backend-architecture.md) with closed authentication, billing, and shared modules; separate HTTP, application, domain, and persistence layers.
- `backend/src/main/resources/db/migration/`: versioned schema and fictional financial fixtures.
- `frontend/`: standalone Angular components, route protection, and responsive screens.
- `compose.yaml`: PostgreSQL, backend, and Angular/Nginx containers.

| Method | Endpoint | Behavior |
| --- | --- | --- |
| GET | /api/health | Basic process health |
| GET | /api/auth/csrf | Obtain CSRF cookie before POST requests |
| POST | /api/auth/login | Form-encoded email/password; 204 on success |
| POST | /api/auth/signup | JSON name/email/password; 201 on success; then sign in |
| POST | /api/auth/logout | Invalidate session; 204 |
| GET | /api/auth/me | Current authenticated user's profile |
| GET | /api/dashboard | Workspace totals and reference date |
| GET | /api/customers | Authorized customer directory |
| GET | /api/invoices | Authorized invoices and authoritative balances |
| GET | /api/invoices/{id} | Invoice, line items, payments, and events |
| GET | /api/transactions | Authorized payment records and events |
| GET | /api/transactions/{id} | Authorized payment evidence |
| POST | /api/chat | Authenticated, CSRF-protected text chat: `{ "messages": [{ "role": "user", "content": "…" }] }` → `{ "reply": "…" }` |

Spring Security uses an HttpOnly session cookie, session fixation protection, and SPA CSRF handling. Angular sends the raw `XSRF-TOKEN` cookie as `X-XSRF-TOKEN` automatically on relative unsafe requests. For manual API clients use the cookie value, not the masked token in the `/csrf` JSON response. Refresh CSRF after authentication or logout. Anonymous reads return 401; invalid/missing CSRF returns 403; absent and unauthorized records both return the same 404. Tenant scope derives from the stored authenticated user. Financial endpoints expose GET only; frontend route guards improve navigation but backend authorization controls access.

Dependency choices: Spring Boot 4.1.1 ([official requirements](https://docs.spring.io/spring-boot/system-requirements.html)), Angular/CLI 22.2.2 and TypeScript 6.0.3 ([Angular compatibility](https://angular.dev/reference/versions)), PostgreSQL 18.6 ([release policy](https://www.postgresql.org/support/versioning/)). Maven manages backend transitive versions; `package-lock.json` locks frontend dependencies.

Backend module boundaries are enforced during `mvn -f backend/pom.xml package -DskipTests` using Spring Modulith 2.1.1 and a build-time architecture checker. Cycles, access to another module's internals, and application/controller dependencies on database adapters fail the build. See [Backend architecture](docs/backend-architecture.md) for ownership, public contracts, transactions, and the extension path for future features.

## Next learning step

The text chat foundation is implemented. F01's record-grounded answers, tools, financial response validation, and injection exercises remain future work, so F01 is not marked complete. No payment provider, refund execution, policy retrieval, or cloud deployment is included.
