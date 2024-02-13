package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.MoneyRequest;
import com.example.wallet.services.WalletService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;

    @PatchMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(@RequestBody MoneyRequest request) {
        return this.walletService.deposit(request);
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@RequestBody MoneyRequest request) {
        return this.walletService.withdraw(request);
    }
}
