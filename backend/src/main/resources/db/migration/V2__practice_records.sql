INSERT INTO tenant VALUES ('T-A', 'Cedar workspace'), ('T-B', 'Harbor workspace');
INSERT INTO customer VALUES
 ('C-A', 'T-A', 'Cedar Labs', 'billing@cedar.example'),
 ('C-A2', 'T-A', 'Northstar Studio', 'billing@northstar.example'),
 ('C-B', 'T-B', 'Harbor Analytics', 'billing@harbor.example');
INSERT INTO invoice VALUES
 ('INV-A101', 'T-A', 'C-A', '2026-10-01', '2026-10-15', 'USD'),
 ('INV-A102', 'T-A', 'C-A2', '2026-09-01', '2026-09-15', 'USD'),
 ('INV-A103', 'T-A', 'C-A', '2026-09-01', '2026-09-15', 'USD'),
 ('INV-A104', 'T-A', 'C-A2', '2026-10-01', '2026-10-20', 'USD'),
 ('INV-B101', 'T-B', 'C-B', '2026-10-01', '2026-10-15', 'USD');
INSERT INTO invoice_line (invoice_id, description, quantity, unit_price) VALUES
 ('INV-A101', 'Monthly subscription', 1, 100.00),
 ('INV-A101', 'Additional seats', 2, 10.00),
 ('INV-A102', 'Studio subscription', 1, 240.00),
 ('INV-A103', 'Monthly subscription', 1, 100.00),
 ('INV-A104', 'Studio subscription', 1, 240.00),
 ('INV-B101', 'Analytics subscription', 1, 900.00);
INSERT INTO payment VALUES
 ('PAY-A201', 'T-A', 'INV-A101', 70.00, 'SETTLED', '2026-10-05', '2026-10-05', 'Bank transfer'),
 ('PAY-A202', 'T-A', 'INV-A101', 50.00, 'PENDING', '2026-10-11', NULL, 'Bank transfer'),
 ('PAY-A203', 'T-A', 'INV-A103', 100.00, 'SETTLED', '2026-09-08', '2026-09-08', 'Card'),
 ('PAY-A204', 'T-A', 'INV-A104', 240.00, 'FAILED', '2026-10-08', NULL, 'Card');
INSERT INTO payment_event VALUES
 ('EV-A301', 'T-A', 'PAY-A201', '2026-10-05', 'Payment became settled'),
 ('EV-A302', 'T-A', 'PAY-A202', '2026-10-11', 'Payment became pending'),
 ('EV-A303', 'T-A', 'PAY-A203', '2026-09-08', 'Payment became settled'),
 ('EV-A304', 'T-A', 'PAY-A204', '2026-10-08', 'Simulated card payment failed');
