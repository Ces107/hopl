package com.hopl.controller;

import com.hopl.dto.auth.LoginRequest;
import com.hopl.dto.auth.RegisterRequest;
import com.hopl.dto.auth.TokenResponse;
import com.hopl.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * @param request registration details
     * @return JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates an existing user.
     *
     * @param request login credentials
     * @return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
