package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.services.WalletService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<ApiResponse> create() {
        return this.walletService.create();
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<ApiResponse> deposit(@PathVariable(value = "id") Long id, @RequestBody Money request) {
        return this.walletService.deposit(id, request);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@PathVariable(value = "id") Long id, @RequestBody Money request) {
        return this.walletService.withdraw(id, request);
    }
}
