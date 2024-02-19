package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PatchMapping("/{username}")
    public ResponseEntity<ApiResponse> transact(@PathVariable(value = "username") String receiver, @RequestBody Money request) {
        return this.transactionService.transact(receiver, request);
    }
}
