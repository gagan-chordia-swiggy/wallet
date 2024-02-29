package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.services.WalletService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> create() {
        return this.walletService.create();
    }

    @PostMapping("/{walletId}/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> deposit(@PathVariable(value = "walletId") Long id, @RequestBody Money moneyRequest) {
        return this.walletService.deposit(id, moneyRequest);
    }

    @PostMapping("/{walletId}/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> withdraw(@PathVariable(value = "walletId") Long id, @RequestBody Money request) {
        return this.walletService.withdraw(id, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getWallets() {
        return this.walletService.getWallets();
    }
}
