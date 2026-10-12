package com.financeops.auth;

import com.financeops.billing.PracticeWorkspaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService users;
    private final PracticeWorkspaceService workspaces;

    public AuthController(UserService users, PracticeWorkspaceService workspaces) {
        this.users = users;
        this.workspaces = workspaces;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @GetMapping("/me")
    public UserService.Profile me(Authentication authentication) {
        return users.profile(authentication.getName());
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody Signup request) {
        workspaces.signup(request.name().strip(), request.email(), request.password());
    }

    public record Signup(@NotBlank @Size(max = 100) String name,
                         @NotBlank @Email @Size(max = 254) String email,
                         @NotBlank @Size(min = 10, max = 64) String password) {
    }
}
