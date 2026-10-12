export interface Profile {
  id: string;
  name: string;
  email: string;
  tenantId: string;
  role: string;
}
export interface Dashboard {
  workspace: string;
  referenceDate: string;
  invoiced: number;
  settled: number;
  pending: number;
  outstanding: number;
  overdue: number;
  invoiceCount: number;
  customerCount: number;
}
export interface Customer {
  id: string;
  name: string;
  email: string;
  invoiceCount: number;
  outstanding: number;
}
export interface Payment {
  id: string;
  invoiceId: string;
  customerName: string;
  amount: number;
  currency: string;
  status: string;
  method: string;
  recordedOn: string;
  settledOn: string | null;
  events: { id: string; occurredOn: string; description: string }[];
}
export interface Invoice {
  id: string;
  customerId: string;
  customerName: string;
  customerEmail: string;
  issuedOn: string;
  dueOn: string;
  currency: string;
  total: number;
  settled: number;
  pending: number;
  outstanding: number;
  status: string;
  overdue: boolean;
  lines: {
    description: string;
    quantity: number;
    unitPrice: number;
    amount: number;
  }[];
  payments: Payment[];
}
