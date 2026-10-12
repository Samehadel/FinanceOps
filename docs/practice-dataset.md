# Expanded practice dataset

Each workspace receives seven additional customers, ten invoices, eighteen payments, and thirty-one payment events. The V4 migration adds these to all existing workspaces without changing their original records or accounts. Future signups receive the same dataset alongside their original starter records. A normal restart does not duplicate fixtures.

Generated record IDs end in the owning tenant ID. For example, Maya's Maple invoice is `INV-D01-T-A`; Omar's is `INV-D01-T-B`. The same customer names in two workspaces represent separate fictional records. All amounts remain USD, and overdue checks use October 12, 2026.

| Invoice prefix | Customer | Total | Settled | Pending | Outstanding | Practice scenario |
| --- | --- | ---: | ---: | ---: | ---: | --- |
| INV-D01 | Maple Commerce | 480.50 | 200.25 | 280.25 | 280.25 | Overdue partial payment; pending remainder |
| INV-D02 | Maple Commerce | 520.00 | 350.00 | 170.00 | 170.00 | Two settled payments plus a pending payment |
| INV-D03 | Aurora Design | 325.75 | 325.75 | 0.00 | 0.00 | Failed card attempt followed by settled bank transfer |
| INV-D04 | Aurora Design | 325.75 | 0.00 | 325.75 | 325.75 | Fully pending; balance still outstanding |
| INV-D05 | Orbit Logistics | 1,250.40 | 1,250.40 | 0.00 | 0.00 | Three settlements pay one invoice |
| INV-D06 | Orbit Logistics | 1,250.40 | 0.00 | 0.00 | 1,250.40 | No payments recorded |
| INV-D07 | Birch Health | 89.97 | 29.99 | 59.98 | 59.98 | Fractional seat prices and overdue remainder |
| INV-D08 | Lumen Education | 215.25 | 215.25 | 0.00 | 0.00 | Fully paid before due date |
| INV-D09 | Atlas Works | 760.10 | 0.00 | 760.10 | 760.10 | Failed attempt plus pending retry; due exactly October 12, not overdue |
| INV-D10 | Atlas Works | 1,499.99 | 999.99 | 0.00 | 500.00 | Older overdue invoice paid in installments |

Summit Ventures has no invoices or transactions, providing a zero-balance customer example. Settled and failed payments include initiation and outcome events; pending payments include only a pending event. No future settlement dates are invented.

Added totals per workspace: **$6,718.11 invoiced**, **$3,371.63 settled**, **$1,596.08 pending**, **$3,346.48 outstanding**, and **$840.23 overdue**. Failed attempts are historical evidence, never deductions from balances.

After expansion:

| Workspace | Customers | Invoices | Payments | Outstanding |
| --- | ---: | ---: | ---: | ---: |
| Maya / Cedar | 9 | 14 | 22 | $3,876.48 |
| Omar / Harbor | 8 | 11 | 18 | $4,246.48 |
| Starter signup workspace | 8 | 11 | 19 | $3,396.48 |

The original INV-A101 ($50 outstanding) and INV-B101 ($900 outstanding, no payments) remain unchanged. Existing workspaces keep any other records already present.

Practice: search for Maple or Atlas, filter overdue invoices, compare pending transactions against outstanding balances, inspect Aurora's failed/successful attempts, and find Summit's empty account.

Verification on October 12, 2026: the live API confirmed all invoice calculations for both demo identities, exact due-date behavior, cross-tenant denial, and expanded signup seeding. Before/after database counts confirmed exact additions in every existing workspace. A subsequent restart left counts unchanged. The temporary verification account was removed.
