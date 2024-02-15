package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.WalletNotFoundException;
import com.example.wallet.models.Wallet;

import com.example.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public ResponseEntity<ApiResponse> create() {
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);

        ApiResponse response = ApiResponse.builder()
                .message("Wallet created")
                .developerMessage("Wallet Created")
                .data(Map.of("wallet", wallet))
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ApiResponse> deposit(Long id, Money request) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(WalletNotFoundException::new);

        wallet.deposit(request);

        ApiResponse response = ApiResponse.builder()
                .message("Amount deposited")
                .developerMessage("Amount deposited")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", wallet))
                .build();

        walletRepository.save(wallet);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse> withdraw(Long id, Money request) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(WalletNotFoundException::new);

        wallet.withdraw(request);

        ApiResponse response = ApiResponse.builder()
                .message("Amount withdrawn")
                .developerMessage("Amount withdrawn")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", wallet))
                .build();

        walletRepository.save(wallet);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse> getWallets() {
        List<Wallet> wallets = walletRepository.findAll();

        ApiResponse response = ApiResponse.builder()
                .message("fetched")
                .developerMessage("fetched")
                .status(HttpStatus.FOUND)
                .statusCode(HttpStatus.FOUND.value())
                .data(Map.of("wallets", wallets))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
