CREATE TABLE tenant (
    id varchar(50) PRIMARY KEY,
    name varchar(100) NOT NULL
);
CREATE TABLE app_user (
    id uuid PRIMARY KEY,
    email varchar(254) NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    password_hash varchar(100) NOT NULL,
    tenant_id varchar(50) NOT NULL REFERENCES tenant(id),
    role varchar(30) NOT NULL CHECK (role = 'SUPPORT')
);
CREATE TABLE customer (
    id varchar(50) PRIMARY KEY,
    tenant_id varchar(50) NOT NULL REFERENCES tenant(id),
    name varchar(100) NOT NULL,
    email varchar(254) NOT NULL,
    UNIQUE (id, tenant_id)
);
CREATE TABLE invoice (
    id varchar(50) PRIMARY KEY,
    tenant_id varchar(50) NOT NULL REFERENCES tenant(id),
    customer_id varchar(50) NOT NULL,
    issued_on date NOT NULL,
    due_on date NOT NULL CHECK (due_on >= issued_on),
    currency varchar(3) NOT NULL CHECK (currency = 'USD'),
    UNIQUE (id, tenant_id),
    FOREIGN KEY (customer_id, tenant_id) REFERENCES customer(id, tenant_id)
);
CREATE TABLE invoice_line (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id varchar(50) NOT NULL REFERENCES invoice(id),
    description varchar(200) NOT NULL,
    quantity integer NOT NULL CHECK (quantity > 0),
    unit_price numeric(14,2) NOT NULL CHECK (unit_price >= 0)
);
CREATE TABLE payment (
    id varchar(50) PRIMARY KEY,
    tenant_id varchar(50) NOT NULL REFERENCES tenant(id),
    invoice_id varchar(50) NOT NULL,
    amount numeric(14,2) NOT NULL CHECK (amount > 0),
    status varchar(20) NOT NULL CHECK (status IN ('SETTLED', 'PENDING', 'FAILED')),
    recorded_on date NOT NULL,
    settled_on date,
    method varchar(30) NOT NULL,
    UNIQUE (id, tenant_id),
    FOREIGN KEY (invoice_id, tenant_id) REFERENCES invoice(id, tenant_id),
    CHECK ((status = 'SETTLED' AND settled_on IS NOT NULL) OR (status <> 'SETTLED' AND settled_on IS NULL))
);
CREATE TABLE payment_event (
    id varchar(50) PRIMARY KEY,
    tenant_id varchar(50) NOT NULL REFERENCES tenant(id),
    payment_id varchar(50) NOT NULL,
    occurred_on date NOT NULL,
    description varchar(200) NOT NULL,
    FOREIGN KEY (payment_id, tenant_id) REFERENCES payment(id, tenant_id)
);
CREATE INDEX invoice_tenant_idx ON invoice(tenant_id);
CREATE INDEX payment_tenant_idx ON payment(tenant_id);
CREATE INDEX customer_tenant_idx ON customer(tenant_id);
CREATE INDEX invoice_line_invoice_idx ON invoice_line(invoice_id);
CREATE INDEX payment_event_payment_idx ON payment_event(payment_id);
