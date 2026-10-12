import { Component, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { ApiService, errorMessage } from "./api.service";

@Component({
  selector: "app-auth",
  imports: [FormsModule, RouterLink],
  templateUrl: "./auth.component.html",
})
export class AuthComponent {
  private api = inject(ApiService);
  private router = inject(Router);
  signup = !!inject(ActivatedRoute).snapshot.data["signup"];
  name = "";
  email = "";
  password = "";
  busy = signal(false);
  error = signal("");

  async submit(): Promise<void> {
    this.busy.set(true);
    this.error.set("");
    try {
      if (this.signup) {
        await this.api.signup(this.name, this.email, this.password);
      } else {
        await this.api.login(this.email, this.password);
      }
      await this.router.navigate(["/overview"]);
    } catch (error) {
      this.error.set(errorMessage(error));
    } finally {
      this.busy.set(false);
    }
  }
}
