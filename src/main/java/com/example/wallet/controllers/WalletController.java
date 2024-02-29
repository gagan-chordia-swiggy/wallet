package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.InvalidWalletAction;
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

    @PatchMapping("/{walletId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> action(@PathVariable(value = "walletId") Long id, @RequestBody Money moneyRequest, @RequestHeader(value = "Action") String action) {
        if (action.equalsIgnoreCase("deposit")) {
            return this.walletService.deposit(id, moneyRequest);
        } else if (action.equalsIgnoreCase("withdraw")) {
            return this.walletService.withdraw(id, moneyRequest);
        }

        throw new InvalidWalletAction();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getWallets() {
        return this.walletService.getWallets();
    }
}
