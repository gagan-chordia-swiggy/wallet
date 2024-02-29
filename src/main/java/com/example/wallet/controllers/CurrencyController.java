package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.CurrencyDTO;
import com.example.wallet.services.CurrencyService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> add(@RequestBody CurrencyDTO request) {
        return this.currencyService.add(request);
    }

    @PutMapping("/currency")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@RequestBody CurrencyDTO request) {
        return this.currencyService.update(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> update(@RequestBody List<CurrencyDTO> request) {
        return this.currencyService.update(request);
    }
}
