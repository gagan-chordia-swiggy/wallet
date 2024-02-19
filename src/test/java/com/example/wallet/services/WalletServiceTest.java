package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.UserRepository;
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

import java.util.List;
import java.util.Objects;

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
    void test_amountDeposited() {
        User user = mock(User.class);
        Wallet wallet = mock(Wallet.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.deposit(new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount deposited", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            Wallet wallet = new Wallet(new Money());
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(user.getWallet()).thenReturn(wallet);
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
        when(user.getWallet()).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.withdraw(new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount withdrawn", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            Wallet wallet = new Wallet(new Money());
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(user.getWallet()).thenReturn(wallet);
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
            Wallet wallet = new Wallet(new Money(0, Currency.INR));
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(user.getWallet()).thenReturn(wallet);
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
}
