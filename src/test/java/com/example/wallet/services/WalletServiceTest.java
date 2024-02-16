package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import com.example.wallet.exceptions.WalletNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        ResponseEntity<ApiResponse> response = walletService.create(mock(User.class));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Wallet created", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_walletNotCreatedForExistingUser_throwsException() {
        User user = mock(User.class);

        walletService.create(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(new Wallet()));
        ResponseEntity<ApiResponse> response = walletService.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User can have only 1 wallet", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_amountDeposited() {
        User user = mock(User.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(new Wallet(new Money())));
        ResponseEntity<ApiResponse> response = walletService.deposit(new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount deposited", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidWalletWhileDepositing_throwsException() {
        assertThrows(WalletNotFoundException.class, () -> {
            User user = mock(User.class);
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByUser(user)).thenThrow(new WalletNotFoundException());
            ResponseEntity<ApiResponse> response = walletService.deposit(new Money(5, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Wallet not found", Objects.requireNonNull(response.getBody()).getMessage());
        });
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(new Wallet(new Money())));
            ResponseEntity<ApiResponse> response = walletService.deposit(new Money(-2, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_amountWithdrawn() {
        Wallet wallet = new Wallet(new Money());
        wallet.deposit(new Money(20, Currency.INR));
        User user = mock(User.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        ResponseEntity<ApiResponse> response = walletService.withdraw(new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount withdrawn", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidWalletWhileWithdrawing_throwsException() {
        assertThrows(WalletNotFoundException.class, () -> {
            User user = mock(User.class);
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByUser(user)).thenThrow(new WalletNotFoundException());
            ResponseEntity<ApiResponse> response = walletService.withdraw(new Money(5, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Wallet not found", Objects.requireNonNull(response.getBody()).getMessage());
        });
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(new Wallet(new Money())));
            ResponseEntity<ApiResponse> response = walletService.withdraw(new Money(-2, Currency.INR));

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
            User user = mock(User.class);
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(new Wallet(new Money())));
            ResponseEntity<ApiResponse> response = walletService.withdraw(new Money(40, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "No sufficient balance",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_whenNoWalletsAreCreated_emptyListIsReturned() {
        ResponseEntity<ApiResponse> response = walletService.getWallets();
        List<Wallet> wallets = (List<Wallet>) Objects.requireNonNull(response.getBody()).getData().get("wallets");

        assertEquals(0, wallets.size());
    }

    @Test
    void test_when2WalletsAreCreated_twoWalletsAreReturned() {
        Wallet wallet1 = (Wallet) Objects.requireNonNull(walletService.create(mock(User.class)).getBody()).getData().get("wallet");
        Wallet wallet2 = (Wallet) Objects.requireNonNull(walletService.create(mock(User.class)).getBody()).getData().get("wallet");

        when(walletRepository.findAll()).thenReturn(Arrays.asList(wallet1, wallet2));
        ResponseEntity<ApiResponse> response = walletService.getWallets();
        List<Wallet> wallets = (List<Wallet>) Objects.requireNonNull(response.getBody()).getData().get("wallets");

        assertEquals(2, wallets.size());
    }
}
