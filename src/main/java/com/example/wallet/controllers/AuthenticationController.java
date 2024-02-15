package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRequest request) {
        return this.authService.register(request);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody UserRequest request) {
        return this.authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request) {
        return this.authService.refreshToken(request);
    }
}
