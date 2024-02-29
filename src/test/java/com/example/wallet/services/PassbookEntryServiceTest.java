package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.exceptions.PassbookEntryNotFoundException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.PassbookEntryRepository;
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
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class PassbookEntryServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PassbookEntryRepository entryRepository;

    @InjectMocks
    private PassbookEntryService entryService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_fetchAllEntriesSuccessfully() {
        User user = mock(User.class);
        String username = "user";
        Wallet wallet = mock(Wallet.class);
        Long walletId = 1L;
        PassbookEntry firstEntry = mock(PassbookEntry.class);
        PassbookEntry secondEntry = mock(PassbookEntry.class);
        List<PassbookEntry> entries = List.of(firstEntry, secondEntry);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(entryRepository.findAllByWallet(wallet)).thenReturn(entries);
        when(firstEntry.getWallet()).thenReturn(wallet);
        when(secondEntry.getWallet()).thenReturn(wallet);
        when(wallet.getId()).thenReturn(walletId);
        ResponseEntity<ApiResponse> response = entryService.fetch(walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fetched", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(walletRepository, times(1)).findByIdAndUser(walletId, user);
        verify(entryRepository, times(1)).findAllByWallet(wallet);
    }

    @Test
    void test_userNotFoundWhileFetchingEntries_throwsException() {
        String username = "user";
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(anyString());
        when(userRepository.findByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = entryService.fetch(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_walletForGivenUserNotFoundWhileFetching_throwsException() {
        User user = mock(User.class);
        String username = "user";
        Long walletId = 1L;
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(walletRepository.findByIdAndUser(walletId, user)).thenThrow(new UnauthorizedWalletAccessException());

        assertThrows(UnauthorizedWalletAccessException.class, () -> {
            ResponseEntity<ApiResponse> response = entryService.fetch(walletId);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("unauthorized wallet access", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_fetchTransactionForGivenTimestampSuccessfully() {
        User user = mock(User.class);
        String username = "user";
        Wallet wallet = mock(Wallet.class);
        Long walletId = 1L;
        Long timestamp = 1L;
        PassbookEntry entry = mock(PassbookEntry.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(entryRepository.findByWalletAndTimestamp(wallet, timestamp)).thenReturn(Optional.of(entry));
        when(entry.getWallet()).thenReturn(wallet);
        when(wallet.getId()).thenReturn(walletId);
        ResponseEntity<ApiResponse> response = entryService.fetchByTimestamp(walletId, timestamp);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fetched", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(walletRepository, times(1)).findByIdAndUser(walletId, user);
        verify(entryRepository, times(1)).findByWalletAndTimestamp(wallet, timestamp);
    }

    @Test
    void test_userNotFoundWhileFetchingEntryWithTimestamp_throwsException() {
        String username = "user";
        Long walletId = 1L;
        Long timestamp = 1L;
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(anyString());
        when(userRepository.findByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = entryService.fetchByTimestamp(walletId, timestamp);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_walletForGivenUserNotFoundWhileFetchingWithTimestamp_throwsException() {
        User user = mock(User.class);
        String username = "user";
        Long walletId = 1L;
        Long timestamp = 1L;
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(walletRepository.findByIdAndUser(walletId, user)).thenThrow(new UnauthorizedWalletAccessException());

        assertThrows(UnauthorizedWalletAccessException.class, () -> {
            ResponseEntity<ApiResponse> response = entryService.fetchByTimestamp(walletId, timestamp);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("unauthorized wallet access", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_passbookEntryNotFoundWhileFetchingByTimestamp_throwsException() {
        User user = mock(User.class);
        String username = "user";
        Wallet wallet = mock(Wallet.class);
        Long walletId = 1L;
        Long timestamp = 1L;
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(walletRepository.findByIdAndUser(walletId, user)).thenReturn(Optional.of(wallet));
        when(entryRepository.findByWalletAndTimestamp(wallet, timestamp)).thenThrow(new PassbookEntryNotFoundException());

        assertThrows(PassbookEntryNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = entryService.fetchByTimestamp(walletId, timestamp);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("no entry found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }
}