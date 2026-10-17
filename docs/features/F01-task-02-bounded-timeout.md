# F01-T02 — Bound model waiting and recover honestly

Status: assigned; implementation and evidence pending. F01 remains active. The learner requested progression without review of T01; T01 completion remains unverified.

## User problem and outcome

A support employee must not wait indefinitely when the model provider is slow. End the waiting state within 15 seconds, show an honest retryable error, and prevent a late response from appearing as a successful answer.

## Scope and rules

- Use the existing invoice chat journey and fictional fixtures. No financial writes.
- The 15-second limit is the total user-visible wait from submitting the chat request, including any retries. A timeout for each individual attempt is insufficient.
- Backend work must have a bounded lifetime too. Explain whether the provider request is cancelled, can continue remotely, or can still incur usage; do not claim cancellation eliminates charges.
- Do not automatically retry for this increment. A user can explicitly retry after failure.
- Never substitute fabricated billing facts for an unavailable model response. Existing authoritative invoice screens can remain accessible.
- No circuit breaker, new job infrastructure, durable conversations, or automated tests in this task.

## First deliverable

Propose a short approach describing where the deadline is enforced, how the UI exits its waiting state, how late responses are handled, and what happens to work still in flight. Keep implementation choices yours. Verify API behavior in official documentation for your pinned client/provider versions before implementing.

## Reproducible manual scenarios

Initial state for each: Maya/U-A signed in to T-A, fresh chat, existing fixtures, INV-A101 USD 50 outstanding. Prompt: “Why do I still owe money on INV-A101?” Capture elapsed time from submission. Financial rows must remain unchanged.

| ID | Steps or injected event | Expected UI and backend outcome |
| --- | --- | --- |
| F01-T02-S01 / F01-S07 | Use a controlled model stub that waits 20 seconds before returning an otherwise valid grounded answer. | Waiting ends within 15 seconds with a retryable timeout error. No fabricated answer. Correlated diagnostics identify timeout. |
| F01-T02-S02 | Repeat S01, then keep observing until at least 25 seconds after submission. | Late answer never appears; no automatic extra model call occurs. Record when backend work actually ends. |
| F01-T02-S03 | After timeout, change the stub to a valid response delivered within one second and explicitly retry once. | One new attempt; error clears and accepted answer displays with evidence. The earlier late response cannot overwrite it. |
| F01-T02-S04 | Configure the stub to fail immediately as provider unavailable. Submit the same question. | Waiting ends promptly; controlled retryable failure, no answer, no raw provider exception or secret disclosure. |

## Learning pack

Needed now: end-to-end deadlines versus per-attempt timeouts, cancellation versus ignoring late results, and retry amplification. These concepts are language-independent. Transport timeout and cancellation settings are specific to the current Spring AI/provider client and frontend request mechanism.

Read [AWS Builders' Library — Timeouts, retries, and backoff with jitter](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/), especially “Timeouts” and “Retries and backoff.” Learn the retry tradeoff; implementing backoff is deferred.

Exercise: a request has three attempts, each with a ten-second timeout. Why does that violate the 15-second acceptance criterion? What happens if the UI stops waiting but the backend continues calling the provider?

Failure exercise after implementation: S02 followed by S03. Controlled faults prove your deadline, failure UX and late-result behavior; they do not prove real-provider cancellation, latency or billing behavior.

## Completion evidence

- Design note and implementation revision.
- Three recorded S01 runs; one run each of S02–S04. Record exact fault configuration, submission time, waiting-end time, final UI, request correlation ID, backend outcome, attempt count and evidence links.
- Inspect routine diagnostics for duration/outcome and content exclusion. Capture token usage when supplied; otherwise mark unavailable. Stub tokens/cost are N/A and do not establish real-provider usage.
- Before/after financial state evidence. No mutations, rejected answers or stale responses should appear.
- Record residual provider work and any cancellation uncertainty explicitly. No reliability claim based solely on stubs.

Completion requires reviewed evidence. T01 and the remaining F01 acceptance criteria are still outstanding until demonstrated.
