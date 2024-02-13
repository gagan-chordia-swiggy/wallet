package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.MoneyRequest;
import com.example.wallet.models.Wallet;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final Wallet wallet;

    public ResponseEntity<ApiResponse> deposit(MoneyRequest request) {
        this.wallet.deposit(request.getAmount());

        ApiResponse response = ApiResponse.builder()
                .message("Amount deposited")
                .developerMessage("Amount deposited")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", this.wallet))
                .build();

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse> withdraw(MoneyRequest request) {
        this.wallet.withdraw(request.getAmount());

        ApiResponse response = ApiResponse.builder()
                .message("Amount withdrawn")
                .developerMessage("Amount withdrawn")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", this.wallet))
                .build();

        return ResponseEntity.ok().body(response);
    }
}
