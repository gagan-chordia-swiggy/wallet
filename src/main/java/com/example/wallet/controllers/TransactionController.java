package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.services.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/wallets/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PatchMapping
    public ResponseEntity<ApiResponse> transact(@RequestBody TransactionRequest request) {
        return this.transactionService.transact(request);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> fetch() {
        return this.transactionService.fetch();
    }

    @GetMapping
    public ResponseEntity<ApiResponse> fetchByTimestamp(@RequestParam(value = "timestamp") Long timestamp) {
        return this.transactionService.fetchByTimestamp(timestamp);
    }
}
