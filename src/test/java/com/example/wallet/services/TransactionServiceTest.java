package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


public class TransactionServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_transactionIsComplete() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Wallet wallet = spy(new Wallet(new Money(300, Currency.INR)));
        Wallet anotherWallet = spy(new Wallet(new Money()));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(anotherUser));
        when(user.getWallet()).thenReturn(wallet);
        when(anotherUser.getWallet()).thenReturn(anotherWallet);
        ResponseEntity<ApiResponse> response = transactionService.transact("user", transactionAmount);

        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, times(1)).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, times(1)).saveAll(List.of(wallet, anotherWallet));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("transaction complete", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_transactionNotCompleteWhenUserNotFound_throwsException() {
        User user = mock(User.class);
        Wallet wallet = spy(new Wallet(new Money(300, Currency.INR)));
        Wallet anotherWallet = spy(new Wallet(new Money()));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact("user", transactionAmount);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, never()).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
    }

    @Test
    void test_transactionNotCompleteWhenInsufficientFunds_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Wallet wallet = spy(new Wallet(new Money(30, Currency.INR)));
        Wallet anotherWallet = spy(new Wallet(new Money()));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(anotherUser));
        when(user.getWallet()).thenReturn(wallet);
        when(anotherUser.getWallet()).thenReturn(anotherWallet);

        assertThrows(OverWithdrawalException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact("user", transactionAmount);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Over withdrawal", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
    }

    @Test
    void test_transactionNotCompleteWhenInvalidAmountIsTransacted_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Wallet wallet = spy(new Wallet(new Money(30, Currency.INR)));
        Wallet anotherWallet = spy(new Wallet(new Money()));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(-6, Currency.INR);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(anotherUser));
        when(user.getWallet()).thenReturn(wallet);
        when(anotherUser.getWallet()).thenReturn(anotherWallet);

        assertThrows(InvalidAmountException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact("user", transactionAmount);
            
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid amount", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
    }
}
