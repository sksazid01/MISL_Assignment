package com.Assignment.controller;

import com.Assignment.dto.*;
import com.Assignment.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // Required for cross-site cookies with SameSite=None
                .path("/")
                .maxAge(maxAge)
                .sameSite("None") // Allow cross-site requests (Vercel -> Render)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(loginRequest);

        // Store tokens in httpOnly cookies with SameSite
        addCookie(response, "accessToken", result.accessToken(), result.accessTokenMaxAge());
        addCookie(response, "refreshToken", result.refreshToken(), result.refreshTokenMaxAge());

        return ResponseEntity.ok(result.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: No refresh token provided!"));
        }

        try {
            AuthService.LoginResult result = authService.refreshToken(refreshToken);

            // Store new tokens in httpOnly cookies with SameSite
            addCookie(response, "accessToken", result.accessToken(), result.accessTokenMaxAge());
            addCookie(response, "refreshToken", result.refreshToken(), result.refreshTokenMaxAge());

            return ResponseEntity.ok(result.authResponse());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Invalid refresh token!"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Not authenticated!"));
        }

        UserResponse userResponse = authService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear cookies by setting maxAge to 0
        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);

        return ResponseEntity.ok(new MessageResponse("Logged out successfully!"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
}