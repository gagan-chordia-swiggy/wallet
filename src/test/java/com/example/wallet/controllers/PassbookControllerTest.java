package com.example.wallet.controllers;

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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PassbookControllerTest {
    @MockBean
    private PassbookEntryService passbookEntryService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        reset(passbookEntryService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_fetchAllEntriesSuccessfully() throws Exception {
        when(passbookEntryService.fetchAll()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/api/v1/entries")).andExpect(status().isOk());
        verify(passbookEntryService, times(1)).fetchAll();
    }

    @Test
    @WithAnonymousUser
    void test_whenFetchingEntriesUserNotFound() throws Exception {
        when(passbookEntryService.fetchAll()).thenThrow(new UserNotFoundException());

        mvc.perform(get("/api/v1/entries")).andExpect(status().isBadRequest());
        verify(passbookEntryService, times(1)).fetchAll();
    }
}