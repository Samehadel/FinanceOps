import { Component, inject, signal } from "@angular/core";
import { CurrencyPipe, DatePipe, TitleCasePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  ActivatedRoute,
  Router,
  RouterLink,
  RouterLinkActive,
} from "@angular/router";
import { ApiService, errorMessage } from "./api.service";
import { Customer, Dashboard, Invoice, Payment } from "./models";
import { ChatComponent } from "./chat.component";

@Component({
  selector: "app-workspace",
  imports: [
    CurrencyPipe,
    DatePipe,
    TitleCasePipe,
    FormsModule,
    RouterLink,
    RouterLinkActive,
    ChatComponent,
  ],
  templateUrl: "./workspace.component.html",
})
export class WorkspaceComponent {
  api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  view = this.route.snapshot.data["view"] as string;
  dashboard = signal<Dashboard | null>(null);
  invoices = signal<Invoice[]>([]);
  customers = signal<Customer[]>([]);
  payments = signal<Payment[]>([]);
  detail = signal<Invoice | null>(null);
  loading = signal(true);
  error = signal("");
  query = signal("");
  status = signal("ALL");
  nav = [
    { path: "overview", label: "Overview", icon: "◫" },
    { path: "invoices", label: "Invoices", icon: "▤" },
    { path: "transactions", label: "Transactions", icon: "⇄" },
    { path: "customers", label: "Customers", icon: "◎" },
  ];

  constructor() {
    void this.load();
  }

  get title(): string {
    return this.view === "detail"
      ? "Invoice details"
      : this.view === "account"
        ? "Your account"
        : this.view === "chat"
          ? "AI assistant"
          : this.view;
  }

  filteredInvoices(): Invoice[] {
    return this.invoices().filter(
      (invoice) =>
        this.matches(invoice.id, invoice.customerName) &&
        (this.status() === "ALL" || invoice.status === this.status()),
    );
  }

  filteredPayments(): Payment[] {
    return this.payments().filter(
      (payment) =>
        this.matches(payment.id, payment.invoiceId, payment.customerName) &&
        (this.status() === "ALL" || payment.status === this.status()),
    );
  }

  filteredCustomers(): Customer[] {
    return this.customers().filter((customer) =>
      this.matches(customer.id, customer.name, customer.email),
    );
  }

  private matches(...values: string[]): boolean {
    return values
      .join(" ")
      .toLowerCase()
      .includes(this.query().toLowerCase().trim());
  }

  async load(): Promise<void> {
    if (this.view === "chat") {
      this.loading.set(false);
      return;
    }
    this.loading.set(true);
    this.error.set("");
    try {
      this.dashboard.set(await this.api.get<Dashboard>("/dashboard"));
      if (this.view === "detail") {
        this.detail.set(
          await this.api.get<Invoice>(
            "/invoices/" + encodeURIComponent(this.route.snapshot.params["id"]),
          ),
        );
      } else if (this.view === "invoices" || this.view === "overview") {
        this.invoices.set(await this.api.get<Invoice[]>("/invoices"));
      } else if (this.view === "transactions") {
        this.payments.set(await this.api.get<Payment[]>("/transactions"));
      } else if (this.view === "customers") {
        this.customers.set(await this.api.get<Customer[]>("/customers"));
      }
    } catch (error) {
      if (!(await this.api.restore())) {
        await this.router.navigate(["/login"]);
      } else {
        this.error.set(errorMessage(error));
      }
    } finally {
      this.loading.set(false);
    }
  }

  async logout(): Promise<void> {
    try {
      await this.api.logout();
      await this.router.navigate(["/login"]);
    } catch (error) {
      this.error.set(errorMessage(error));
    }
  }
}
