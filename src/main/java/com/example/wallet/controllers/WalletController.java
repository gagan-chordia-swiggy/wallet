package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.services.WalletService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PatchMapping("/{walletId}/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> deposit(@PathVariable(value = "walletId") Long id, @RequestBody Money moneyRequest) throws UnauthorizedWalletAccessException {
        return this.walletService.deposit(id, moneyRequest);
    }

    @PatchMapping("/{walletId}/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> withdraw(@PathVariable(value = "walletId") Long id, @RequestBody Money request) throws UnauthorizedWalletAccessException {
        return this.walletService.withdraw(id, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getWallets() {
        return this.walletService.getWallets();
    }
}
