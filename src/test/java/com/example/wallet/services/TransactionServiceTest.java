package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.enums.Currency;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.*;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.Transaction;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.PassbookRepository;
import com.example.wallet.repository.TransactionRepository;
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

import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PassbookRepository passbookRepository;


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
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(300, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(), anotherUser));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenReturn(Optional.of(anotherWallet));
        ResponseEntity<ApiResponse> response = transactionService.transact(request);

        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, times(1)).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, times(1)).saveAll(List.of(wallet, anotherWallet));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(passbookRepository, times(1)).saveAll(any(List.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("transaction complete", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_transactionIsCompleteForReceiverWithDifferentCurrency() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(30000, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(Currency.GBP), anotherUser));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(10000, Currency.INR);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenReturn(Optional.of(anotherWallet));
        ResponseEntity<ApiResponse> response = transactionService.transact(request);

        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, times(1)).deposit(new Money(100.0, Currency.GBP));
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, times(1)).saveAll(List.of(wallet, anotherWallet));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(passbookRepository, times(1)).saveAll(any(List.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("transaction complete", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_transactionNotCompleteWhenUserTriesToTransactInDifferentCurrency_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(30000, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(Currency.GBP), anotherUser));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(10000, Currency.GBP);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenReturn(Optional.of(anotherWallet));

        assertThrows(IncompatibleCurrencyException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("incompatible currency", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_transactionNotCompleteWhenUserNotFound_throwsException() {
        User user = mock(User.class);
        Wallet wallet = spy(new Wallet(new Money(300, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(), mock(User.class)));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);
        TransactionRequest request = new TransactionRequest("username", 1L, 2L, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, never()).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
        verify(transactionRepository, never()).saveAll(any(List.class));
    }

    @Test
    void test_transactionIsNotCompleteWhenUnknownWalletForUserIsAccessedForTransaction_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(30000, Currency.INR), user));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(10000, Currency.GBP);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenThrow(new UnauthorizedWalletAccessException());

        assertThrows(UnauthorizedWalletAccessException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("unauthorized wallet access", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_transactionNotCompleteWhenInsufficientFunds_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(30, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(), anotherUser));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(100, Currency.INR);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenReturn(Optional.of(anotherWallet));

        assertThrows(OverWithdrawalException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Over withdrawal", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
        verify(transactionRepository, never()).saveAll(any(List.class));
    }

    @Test
    void test_transactionNotCompleteWhenInvalidAmountIsTransacted_throwsException() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        Long walletId = 1L;
        Long anotherWalletId = 2L;
        Wallet wallet = spy(new Wallet(new Money(30, Currency.INR), user));
        Wallet anotherWallet = spy(new Wallet(new Money(), anotherUser));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Money transactionAmount = new Money(-6, Currency.INR);
        TransactionRequest request = new TransactionRequest("username", walletId, anotherWalletId, transactionAmount);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(anotherUser));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByIdAndUser(anotherWalletId, anotherUser)).thenReturn(Optional.of(anotherWallet));

        assertThrows(InvalidAmountException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.transact(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid amount", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(wallet, times(1)).withdraw(transactionAmount);
        verify(wallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).deposit(transactionAmount);
        verify(anotherWallet, never()).withdraw(transactionAmount);
        verify(walletRepository, never()).saveAll(List.of(wallet, anotherWallet));
        verify(transactionRepository, never()).saveAll(any(List.class));
    }

    @Test
    void test_fetchAllTransactionsOfUserSuccessfully() {
        User user = mock(User.class);
        User anotherUser = mock(User.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        Transaction firstTransaction = mock(Transaction.class);
        Transaction secondTransaction = mock(Transaction.class);
        PassbookEntry entry = spy(new PassbookEntry(1L, System.currentTimeMillis(), mock(Wallet.class), new Money(), 0.0, TransactionType.DEPOSIT));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(firstTransaction.getSender()).thenReturn(user);
        when(secondTransaction.getSender()).thenReturn(user);
        when(firstTransaction.getReceiver()).thenReturn(anotherUser);
        when(secondTransaction.getReceiver()).thenReturn(anotherUser);
        when(user.getUsername()).thenReturn("user");
        when(firstTransaction.getReceiverEntry()).thenReturn(entry);
        when(firstTransaction.getSenderEntry()).thenReturn(entry);
        when(secondTransaction.getReceiverEntry()).thenReturn(entry);
        when(secondTransaction.getSenderEntry()).thenReturn(entry);
        when(transactionRepository.findAllBySenderOrReceiver(user, user)).thenReturn(List.of(firstTransaction, secondTransaction));
        ResponseEntity<ApiResponse> response = transactionService.fetch();

        verify(transactionRepository, times(1)).findAllBySenderOrReceiver(user, user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fetched", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_unknownUserWhileFetching_throwsException() {
        User user = mock(User.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = transactionService.fetch();

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
        verify(transactionRepository, never()).findAllBySenderOrReceiver(user, user);
    }
}