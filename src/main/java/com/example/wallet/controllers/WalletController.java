package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.services.WalletService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PatchMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> deposit(@RequestBody Money moneyRequest) {
        return this.walletService.deposit(moneyRequest);
    }

    @PatchMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> withdraw(@RequestBody Money request) {
        return this.walletService.withdraw(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getWallets() {
        return this.walletService.getWallets();
    }

}
