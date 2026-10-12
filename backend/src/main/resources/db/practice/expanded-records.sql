INSERT INTO customer (id, tenant_id, name, email)
SELECT 'C-D' || sample.code || '-' || t.id, t.id, sample.name, sample.email
FROM tenant t
CROSS JOIN (VALUES
    ('01', 'Maple Commerce', 'billing@maple.example'),
    ('02', 'Aurora Design', 'billing@aurora.example'),
    ('03', 'Orbit Logistics', 'billing@orbit.example'),
    ('04', 'Birch Health', 'billing@birch.example'),
    ('05', 'Lumen Education', 'billing@lumen.example'),
    ('06', 'Atlas Works', 'billing@atlas.example'),
    ('07', 'Summit Ventures', 'billing@summit.example')
) AS sample(code, name, email)
WHERE t.id = coalesce(?, t.id);

INSERT INTO invoice (id, tenant_id, customer_id, issued_on, due_on, currency)
SELECT 'INV-D' || sample.code || '-' || t.id, t.id, 'C-D' || sample.customer || '-' || t.id, sample.issued::date, sample.due::date, 'USD'
FROM tenant t
CROSS JOIN (VALUES
    ('01', '01', '2026-09-01', '2026-09-15'),
    ('02', '01', '2026-10-01', '2026-10-20'),
    ('03', '02', '2026-09-01', '2026-09-20'),
    ('04', '02', '2026-10-01', '2026-10-15'),
    ('05', '03', '2026-09-01', '2026-09-30'),
    ('06', '03', '2026-10-01', '2026-10-30'),
    ('07', '04', '2026-09-01', '2026-09-15'),
    ('08', '05', '2026-10-01', '2026-10-18'),
    ('09', '06', '2026-10-01', '2026-10-12'),
    ('10', '06', '2026-08-01', '2026-08-31')
) AS sample(code, customer, issued, due)
WHERE t.id = coalesce(?, t.id);

INSERT INTO invoice_line (invoice_id, description, quantity, unit_price)
SELECT 'INV-D' || sample.invoice || '-' || t.id, sample.description, sample.quantity::integer, sample.unit_price::numeric
FROM tenant t
CROSS JOIN (VALUES
    ('01', 'Commerce subscription', '1', '400.00'),
    ('01', 'Additional seats', '7', '11.50'),
    ('02', 'Commerce subscription', '1', '400.00'),
    ('02', 'Additional seats', '10', '12.00'),
    ('03', 'Design subscription', '1', '300.00'),
    ('03', 'Storage add-on', '1', '25.75'),
    ('04', 'Design subscription', '1', '300.00'),
    ('04', 'Storage add-on', '1', '25.75'),
    ('05', 'Logistics platform', '1', '1000.00'),
    ('05', 'Tracking seats', '12', '20.00'),
    ('05', 'Reporting add-on', '1', '10.40'),
    ('06', 'Logistics platform', '1', '1000.00'),
    ('06', 'Tracking seats', '12', '20.00'),
    ('06', 'Reporting add-on', '1', '10.40'),
    ('07', 'Clinic seats', '3', '29.99'),
    ('08', 'Education subscription', '1', '199.00'),
    ('08', 'Extra classrooms', '5', '3.25'),
    ('09', 'Team subscription', '1', '700.00'),
    ('09', 'Extra seats', '6', '10.00'),
    ('09', 'Usage add-on', '1', '0.10'),
    ('10', 'Annual workspace subscription', '1', '1499.99')
) AS sample(invoice, description, quantity, unit_price)
WHERE t.id = coalesce(?, t.id);

INSERT INTO payment (id, tenant_id, invoice_id, amount, status, recorded_on, settled_on, method)
SELECT 'PAY-D' || sample.code || '-' || t.id, t.id, 'INV-D' || sample.invoice || '-' || t.id, sample.amount::numeric, sample.status, sample.recorded::date, sample.settled::date, sample.method
FROM tenant t
CROSS JOIN (VALUES
    ('01a', '01', '200.25', 'SETTLED', '2026-09-05', '2026-09-07', 'Bank transfer'),
    ('01b', '01', '280.25', 'PENDING', '2026-10-10', NULL, 'Bank transfer'),
    ('02a', '02', '150.00', 'SETTLED', '2026-10-02', '2026-10-03', 'Card'),
    ('02b', '02', '200.00', 'SETTLED', '2026-10-05', '2026-10-07', 'Bank transfer'),
    ('02c', '02', '170.00', 'PENDING', '2026-10-11', NULL, 'Bank transfer'),
    ('03a', '03', '325.75', 'FAILED', '2026-09-04', NULL, 'Card'),
    ('03b', '03', '325.75', 'SETTLED', '2026-09-06', '2026-09-08', 'Bank transfer'),
    ('04a', '04', '325.75', 'PENDING', '2026-10-09', NULL, 'Card'),
    ('05a', '05', '500.00', 'SETTLED', '2026-09-03', '2026-09-05', 'Bank transfer'),
    ('05b', '05', '400.00', 'SETTLED', '2026-09-10', '2026-09-12', 'Bank transfer'),
    ('05c', '05', '350.40', 'SETTLED', '2026-09-20', '2026-09-22', 'Card'),
    ('07a', '07', '29.99', 'SETTLED', '2026-09-03', '2026-09-04', 'Card'),
    ('07b', '07', '59.98', 'PENDING', '2026-10-08', NULL, 'Bank transfer'),
    ('08a', '08', '215.25', 'SETTLED', '2026-10-04', '2026-10-06', 'Card'),
    ('09a', '09', '760.10', 'FAILED', '2026-10-04', NULL, 'Card'),
    ('09b', '09', '760.10', 'PENDING', '2026-10-11', NULL, 'Bank transfer'),
    ('10a', '10', '499.99', 'SETTLED', '2026-08-04', '2026-08-06', 'Bank transfer'),
    ('10b', '10', '500.00', 'SETTLED', '2026-09-04', '2026-09-06', 'Bank transfer')
) AS sample(code, invoice, amount, status, recorded, settled, method)
WHERE t.id = coalesce(?, t.id);

INSERT INTO payment_event (id, tenant_id, payment_id, occurred_on, description)
SELECT 'EV-D' || sample.code || '-' || t.id, t.id, 'PAY-D' || sample.payment || '-' || t.id, sample.occurred::date, sample.description
FROM tenant t
CROSS JOIN (VALUES
    ('01aR', '01a', '2026-09-05', 'Payment initiated, awaiting confirmation'),
    ('01aF', '01a', '2026-09-07', 'Payment became settled'),
    ('01bR', '01b', '2026-10-10', 'Payment pending, settlement timing unknown'),
    ('02aR', '02a', '2026-10-02', 'Payment initiated, awaiting confirmation'),
    ('02aF', '02a', '2026-10-03', 'Payment became settled'),
    ('02bR', '02b', '2026-10-05', 'Payment initiated, awaiting confirmation'),
    ('02bF', '02b', '2026-10-07', 'Payment became settled'),
    ('02cR', '02c', '2026-10-11', 'Payment pending, settlement timing unknown'),
    ('03aR', '03a', '2026-09-04', 'Payment initiated, awaiting confirmation'),
    ('03aF', '03a', '2026-09-05', 'Simulated payment failed'),
    ('03bR', '03b', '2026-09-06', 'Payment initiated, awaiting confirmation'),
    ('03bF', '03b', '2026-09-08', 'Payment became settled'),
    ('04aR', '04a', '2026-10-09', 'Payment pending, settlement timing unknown'),
    ('05aR', '05a', '2026-09-03', 'Payment initiated, awaiting confirmation'),
    ('05aF', '05a', '2026-09-05', 'Payment became settled'),
    ('05bR', '05b', '2026-09-10', 'Payment initiated, awaiting confirmation'),
    ('05bF', '05b', '2026-09-12', 'Payment became settled'),
    ('05cR', '05c', '2026-09-20', 'Payment initiated, awaiting confirmation'),
    ('05cF', '05c', '2026-09-22', 'Payment became settled'),
    ('07aR', '07a', '2026-09-03', 'Payment initiated, awaiting confirmation'),
    ('07aF', '07a', '2026-09-04', 'Payment became settled'),
    ('07bR', '07b', '2026-10-08', 'Payment pending, settlement timing unknown'),
    ('08aR', '08a', '2026-10-04', 'Payment initiated, awaiting confirmation'),
    ('08aF', '08a', '2026-10-06', 'Payment became settled'),
    ('09aR', '09a', '2026-10-04', 'Payment initiated, awaiting confirmation'),
    ('09aF', '09a', '2026-10-05', 'Simulated payment failed'),
    ('09bR', '09b', '2026-10-11', 'Payment pending, settlement timing unknown'),
    ('10aR', '10a', '2026-08-04', 'Payment initiated, awaiting confirmation'),
    ('10aF', '10a', '2026-08-06', 'Payment became settled'),
    ('10bR', '10b', '2026-09-04', 'Payment initiated, awaiting confirmation'),
    ('10bF', '10b', '2026-09-06', 'Payment became settled')
) AS sample(code, payment, occurred, description)
WHERE t.id = coalesce(?, t.id);
