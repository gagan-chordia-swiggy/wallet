package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;

import com.example.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public ResponseEntity<ApiResponse> deposit(Money moneyRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        Wallet wallet = user.getWallet();

        wallet.deposit(moneyRequest);

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

    public ResponseEntity<ApiResponse> withdraw(Money request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        Wallet wallet = user.getWallet();

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
