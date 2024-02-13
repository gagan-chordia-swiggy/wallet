package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.MoneyRequest;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import com.example.wallet.models.Wallet;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletServiceTest {
    @Test
    void test_amountDeposited() {
        Wallet wallet = new Wallet();
        WalletService walletService = new WalletService(wallet);

        ResponseEntity<ApiResponse> response = walletService.deposit(new MoneyRequest(15));

        assertEquals(15, wallet.getBalance());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallet, Objects.requireNonNull(response.getBody()).getData().get("wallet"));
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        WalletService walletService = new WalletService(new Wallet());

        assertThrows(InvalidAmountException.class, () -> {
            ResponseEntity<ApiResponse> response = walletService.deposit(new MoneyRequest(-2));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_amountWithdrawn() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        ResponseEntity<ApiResponse> response = walletService.withdraw(new MoneyRequest(10));

        assertEquals(20, wallet.getBalance());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallet, Objects.requireNonNull(response.getBody()).getData().get("wallet"));
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        assertThrows(InvalidAmountException.class, () -> {
            ResponseEntity<ApiResponse> response = walletService.withdraw(new MoneyRequest(-2));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_OverWithdrawal_throwsException() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        assertThrows(OverWithdrawalException.class, () -> {
            ResponseEntity<ApiResponse> response = walletService.withdraw(new MoneyRequest(40));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "No sufficient balance",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }
}
