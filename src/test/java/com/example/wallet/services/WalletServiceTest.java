package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.enums.Currency;
import com.example.wallet.enums.Location;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.PassbookRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PassbookRepository passbookRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_walletIsCreatedWithoutAuthenticatedUser_successfully() {
        User user = User.builder()
                .location(Location.INDIA)
                .build();
        Wallet wallet = new Wallet(user);

        when(walletRepository.save(wallet)).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.create(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wallet created", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        WalletResponse walletResponse = (WalletResponse) Objects.requireNonNull(response.getBody()).getData().get("wallet");
        assertEquals(Currency.INR, walletResponse.getMoney().getCurrency());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void test_walletForUserInUnitedStatesHasDefaultCurrencyUSD() {
        User user = User.builder()
                .location(Location.UNITED_STATES)
                .build();
        Wallet wallet = new Wallet(user);

        when(walletRepository.save(wallet)).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.create(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wallet created", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        WalletResponse walletResponse = (WalletResponse) Objects.requireNonNull(response.getBody()).getData().get("wallet");
        assertEquals(Currency.USD, walletResponse.getMoney().getCurrency());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void test_walletForUserInBritainHasDefaultCurrencyGBP() {
        User user = User.builder()
                .location(Location.BRITAIN)
                .build();
        Wallet wallet = new Wallet(user);

        when(walletRepository.save(wallet)).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.create(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wallet created", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        WalletResponse walletResponse = (WalletResponse) Objects.requireNonNull(response.getBody()).getData().get("wallet");
        assertEquals(Currency.GBP, walletResponse.getMoney().getCurrency());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void test_newWalletCreatedForAuthenticatedUser_successfully() {
        User user = User.builder()
                .location(Location.INDIA)
                .build();
        Wallet wallet = new Wallet(user);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(walletRepository.save(wallet)).thenReturn(wallet);
        ResponseEntity<ApiResponse> response = walletService.create();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wallet created", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void test_noNewWalletCreatedForRegisteredUserButUnauthenticated_throwsException() {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);
        when(userRepository.findByUsername(null)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = walletService.create();

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_amountDeposited_successfully() {
        User user = mock(User.class);
        Long walletId = 1L;
        Wallet wallet = mock(Wallet.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(wallet.getId()).thenReturn(1L);
        when(wallet.getMoney()).thenReturn(new Money());
        when(user.getLocation()).thenReturn(Location.INDIA);
        ResponseEntity<ApiResponse> response = walletService.deposit(walletId, new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount deposited", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            Long walletId = 1L;
            Wallet wallet = spy(new Wallet(new Money(), user));
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
            when(wallet.getId()).thenReturn(1L);
            ResponseEntity<ApiResponse> response = walletService.deposit(1L, new Money(-2, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "Non positive amount has been entered",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_depositInUnauthorizedWallet_throwsException() {
        User user = mock(User.class);
        Long walletId = 1L;
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findByIdAndUser(walletId, user)).thenThrow(new UnauthorizedWalletAccessException());

        assertThrows(UnauthorizedWalletAccessException.class, () -> {
            ResponseEntity<ApiResponse> response = walletService.deposit(walletId, new Money(5, Currency.INR));

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals(
                    "Access to other wallets is not permitted",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_amountWithdrawn() throws UnauthorizedWalletAccessException {
        User user = mock(User.class);
        Long walletId = 1L;
        Wallet wallet = spy(new Wallet(new Money(), user));
        wallet.deposit(new Money(20, Currency.INR));
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(user.getLocation()).thenReturn(Location.INDIA);
        ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(10, Currency.INR));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amount withdrawn", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        assertThrows(InvalidAmountException.class, () -> {
            User user = mock(User.class);
            Long walletId = 1L;
            Wallet wallet = spy(new Wallet(new Money(), user));
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
            ResponseEntity<ApiResponse> response = walletService.withdraw(walletId, new Money(-2, Currency.INR));

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
            Long walletId = 1L;
            Wallet wallet = spy(new Wallet(new Money(0, Currency.INR), user));
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
            ResponseEntity<ApiResponse> response = walletService.withdraw(walletId, new Money(40, Currency.INR));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(
                    "No sufficient balance",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_withdrawFromInUnauthorizedWallet_throwsException() {
        assertThrows(UnauthorizedWalletAccessException.class, () -> {
            User user = mock(User.class);
            Long walletId = 2L;
            Wallet wallet = spy(new Wallet(new Money(), user));
            SecurityContext context = mock(SecurityContext.class);
            SecurityContextHolder.setContext(context);
            Authentication authentication = mock(Authentication.class);

            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
            ResponseEntity<ApiResponse> response = walletService.withdraw(1L, new Money(-2, Currency.INR));

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals(
                    "Access to other wallets is not permitted",
                    Objects.requireNonNull(response.getBody()).getMessage()
            );
        });
    }

    @Test
    void test_whenTwoWalletsAreCreatedForUser_TwoWalletsAreReturned() {
        Wallet firstWallet = mock(Wallet.class);
        Wallet secondWallet = mock(Wallet.class);
        Wallet thirdWallet = mock(Wallet.class);
        User user = mock(User.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletRepository.findAllByUser(user)).thenReturn(List.of(firstWallet, thirdWallet));
        ResponseEntity<ApiResponse> response = walletService.getWallets();

        List<Wallet> wallets = (List<Wallet>) Objects.requireNonNull(response.getBody()).getData().get("wallets");
        assertEquals(2, wallets.size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
