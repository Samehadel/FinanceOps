package com.financeops.auth.api;

import com.financeops.auth.application.SignupService;
import com.financeops.auth.contract.CurrentUser;
import com.financeops.auth.api.dto.ProfileResponse;
import com.financeops.auth.api.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CurrentUser users;
    private final SignupService signup;

    public AuthController(CurrentUser users, SignupService signup) {
        this.users = users;
        this.signup = signup;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @GetMapping("/me")
    public ProfileResponse me() {
        return ProfileResponse.from(users.profile());
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequest request) {
        signup.signup(request.name(), request.email(), request.password());
    }
}
