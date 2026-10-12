import { inject } from "@angular/core";
import { CanActivateFn, Router, Routes } from "@angular/router";
import { ApiService } from "./api.service";
import { AuthComponent } from "./auth.component";
import { WorkspaceComponent } from "./workspace.component";

const authenticated: CanActivateFn = async () => {
  const api = inject(ApiService);
  const router = inject(Router);
  return api.profile() || (await api.restore())
    ? true
    : router.createUrlTree(["/login"]);
};

export const routes: Routes = [
  { path: "login", component: AuthComponent },
  { path: "signup", component: AuthComponent, data: { signup: true } },
  ...["overview", "invoices", "transactions", "customers", "account"].map(
    (path) => ({
      path,
      component: WorkspaceComponent,
      canActivate: [authenticated],
      data: { view: path },
    }),
  ),
  {
    path: "invoices/:id",
    component: WorkspaceComponent,
    canActivate: [authenticated],
    data: { view: "detail" },
  },
  { path: "", pathMatch: "full", redirectTo: "overview" },
  { path: "**", redirectTo: "overview" },
];
