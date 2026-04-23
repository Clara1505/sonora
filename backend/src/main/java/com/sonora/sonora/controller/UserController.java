package com.sonora.sonora.controller;

import com.sonora.sonora.dto.AuthResponse;
import com.sonora.sonora.dto.LoginRequest;
import com.sonora.sonora.dto.RegisterRequest;
import com.sonora.sonora.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for public authentication endpoints. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class UserController {

    /** Authentication service. */
    private final AuthService authService;

    /**
     * Creates user controller.
     *
     * @param service authentication service
     */
    public UserController(final AuthService service) {
        this.authService = service;
    }

    /**
     * Registers a new user.
     *
     * @param request registration payload
     * @return authentication response with token
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody final RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Logs in an existing user.
     *
     * @param request login payload
     * @return authentication response with token
     */
    @PostMapping("/login")
    @Operation(summary = "Login an existing user")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
