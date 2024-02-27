package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import com.example.wallet.repository.PassbookRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class PassbookEntryServiceTest {
    @Mock
    private PassbookRepository passbookRepository;

    @InjectMocks
    private PassbookEntryService passbookEntryService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_fetchAllEntriesSuccessfully() {
        User user = mock(User.class);
        PassbookEntry passbookEntry = mock(PassbookEntry.class);
        PassbookEntry anotherPassbookEntry = mock(PassbookEntry.class);
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);
        List<PassbookEntry> entries = List.of(passbookEntry, anotherPassbookEntry);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(passbookEntry.getUser()).thenReturn(user);
        when(anotherPassbookEntry.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(anyString());
        when(passbookRepository.findAllByUser(user)).thenReturn(entries);
        ResponseEntity<ApiResponse> response = passbookEntryService.fetchAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fetched", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_whenFetchingEntriesUserNotFound() {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Authentication authentication = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            ResponseEntity<ApiResponse> response = passbookEntryService.fetchAll();

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("user not found", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }
}