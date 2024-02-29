package com.example.wallet.controllers;

import com.example.wallet.exceptions.PassbookEntryNotFoundException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.services.PassbookEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PassbookEntryControllerTest {
    @MockBean
    private PassbookEntryService entryService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        reset(entryService);
    }

    @Test
    void test_fetchAllEntriesSuccessfully() throws Exception {
        when(entryService.fetch(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/api/v1/entries?walletId=1")).andExpect(status().isOk());
        verify(entryService, times(1)).fetch(1L);
    }

    @Test
    void test_userNotFoundWhileFetchingEntries_badRequest() throws Exception {
        when(entryService.fetch(1L)).thenThrow(new UserNotFoundException());

        mvc.perform(get("/api/v1/entries?walletId=1")).andExpect(status().isBadRequest());
        verify(entryService, times(1)).fetch(1L);
    }

    @Test
    void test_test_walletForGivenUserNotFoundWhileFetching_unauthorized() throws Exception {
        when(entryService.fetch(1L)).thenThrow(new UnauthorizedWalletAccessException());

        mvc.perform(get("/api/v1/entries?walletId=1")).andExpect(status().isUnauthorized());
        verify(entryService, times(1)).fetch(1L);
    }

    @Test
    void test_fetchTransactionForGivenTimestampSuccessfully() throws Exception {
        when(entryService.fetch(1L, 1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/api/v1/entries?walletId=1&timestamp=1")).andExpect(status().isOk());
        verify(entryService, times(1)).fetch(1L, 1L);
    }

    @Test
    void test_userNotFoundWhileFetchingEntryWithTimestamp_badRequest() throws Exception {
        when(entryService.fetch(1L, 1L)).thenThrow(new UserNotFoundException());

        mvc.perform(get("/api/v1/entries?walletId=1&timestamp=1")).andExpect(status().isBadRequest());
        verify(entryService, times(1)).fetch(1L, 1L);
    }

    @Test
    void test_walletForGivenUserNotFoundWhileFetchingWithTimestamp_unauthorized() throws Exception {
        when(entryService.fetch(1L, 1L)).thenThrow(new UnauthorizedWalletAccessException());

        mvc.perform(get("/api/v1/entries?walletId=1&timestamp=1")).andExpect(status().isUnauthorized());
        verify(entryService, times(1)).fetch(1L, 1L);
    }

    @Test
    void test_passbookEntryNotFoundWhileFetchingByTimestamp_notFound() throws Exception {
        when(entryService.fetch(1L, 1L)).thenThrow(new PassbookEntryNotFoundException());

        mvc.perform(get("/api/v1/entries?walletId=1&timestamp=1")).andExpect(status().isNotFound());
        verify(entryService, times(1)).fetch(1L, 1L);
    }
}