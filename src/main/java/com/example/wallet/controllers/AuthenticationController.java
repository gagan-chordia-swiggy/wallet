package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.services.AuthenticationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authService;

    @PostMapping
    public ResponseEntity<ApiResponse> register(@RequestBody UserRequest request) {
        return this.authService.register(request);
    }

    @PostMapping("/auth")
    public ResponseEntity<ApiResponse> login(@RequestBody UserRequest request) {
        return this.authService.login(request);
    }
}
