package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import com.example.wallet.exceptions.WalletNotFoundException;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_walletIsCreated() {
        ResponseEntity<ApiResponse> response = walletService.create();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Wallet created", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_amountDeposited() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, new Money())));
        ResponseEntity<ApiResponse> response = walletService.deposit(1L, new Money(10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount deposited", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidWalletWhileDepositing_throwsException() {
        assertThrows(WalletNotFoundException.class, () -> {
            when(walletRepository.findById(1L)).thenReturn(Optional.empty());
            ResponseEntity<ApiResponse> response = walletService.deposit(1L, new Money(5));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Wallet not found", Objects.requireNonNull(response.getBody()).getMessage());
        });
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, new Money())));
            ResponseEntity<ApiResponse> response = walletService.deposit(1L, new Money(-2));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_amountWithdrawn() {
        Wallet wallet = new Wallet(1L, new Money());
        wallet.deposit(new Money(20));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount withdrawn", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidWalletWhileWithdrawing_throwsException() {
        assertThrows(WalletNotFoundException.class, () -> {
            when(walletRepository.findById(1L)).thenReturn(Optional.empty());
            ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(5));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Wallet not found", Objects.requireNonNull(response.getBody()).getMessage());
        });
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, new Money())));
            ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(-2));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_OverWithdrawal_throwsException() {
        assertThrows(OverWithdrawalException.class, () -> {
            when(walletRepository.findById(1L)).thenReturn(Optional.of(new Wallet(1L, new Money())));
            ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(40));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "No sufficient balance",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }
}
