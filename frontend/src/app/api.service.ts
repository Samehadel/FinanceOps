import { inject, Injectable, signal } from "@angular/core";
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from "@angular/common/http";
import { firstValueFrom } from "rxjs";
import { Profile } from "./models";

@Injectable({ providedIn: "root" })
export class ApiService {
  private http = inject(HttpClient);
  profile = signal<Profile | null>(null);

  get<T>(path: string): Promise<T> {
    return firstValueFrom(this.http.get<T>("/api" + path));
  }

  async restore(): Promise<boolean> {
    try {
      this.profile.set(await this.get<Profile>("/auth/me"));
      return true;
    } catch {
      this.profile.set(null);
      return false;
    }
  }

  async login(email: string, password: string): Promise<void> {
    await this.get("/auth/csrf");
    const body = new HttpParams()
      .set("email", email.trim().toLowerCase())
      .set("password", password);
    await firstValueFrom(this.http.post("/api/auth/login", body));
    await this.get("/auth/csrf");
    this.profile.set(await this.get<Profile>("/auth/me"));
  }

  async signup(name: string, email: string, password: string): Promise<void> {
    await this.get("/auth/csrf");
    await firstValueFrom(
      this.http.post("/api/auth/signup", {
        name,
        email: email.trim(),
        password,
      }),
    );
    await this.login(email, password);
  }

  async logout(): Promise<void> {
    await this.get("/auth/csrf");
    await firstValueFrom(this.http.post("/api/auth/logout", {}));
    this.profile.set(null);
  }
}

export function errorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    return (
      error.error?.message ||
      (error.status === 0
        ? "Cannot connect to FinanceOps. Check that the backend is running."
        : "Something went wrong. Please try again.")
    );
  }
  return "Something went wrong. Please try again.";
}
