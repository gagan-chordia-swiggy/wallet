package com.example.wallet.controllers;

import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.*;
import com.example.wallet.services.TransactionService;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        reset(transactionService);
    }

    @Test
    void test_transactionIsComplete() throws Exception {
        Money money = new Money(50, Currency.INR);
        TransactionRequest transaction = new TransactionRequest("user", 1L, 2L, money);
        String request = mapper.writeValueAsString(transaction);

        when(transactionService.transact(transaction)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/api/v1/transactions")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());
        verify(transactionService, times(1)).transact(transaction);
    }

    @Test
    void test_transactionNotCompleteWhenUserNotFound_throwsException() throws Exception {
        Money money = new Money(50, Currency.INR);
        TransactionRequest transaction = new TransactionRequest("user", 1L, 2L, money);
        String request = mapper.writeValueAsString(transaction);

        when(transactionService.transact(transaction)).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/api/v1/transactions")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact(transaction);
    }

    @Test
    void test_transactionNotCompleteWhenInsufficientFunds_throwsException() throws Exception {
        Money money = new Money(50, Currency.INR);
        TransactionRequest transaction = new TransactionRequest("user", 1L, 2L, money);
        String request = mapper.writeValueAsString(transaction);

        when(transactionService.transact(transaction)).thenThrow(new OverWithdrawalException());

        mockMvc.perform(post("/api/v1/transactions")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact(transaction);
    }

    @Test
    void test_transactionNotCompleteWhenInvalidAmountIsTransacted_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        TransactionRequest transaction = new TransactionRequest("user", 1L, 2L, money);
        String request = mapper.writeValueAsString(transaction);

        when(transactionService.transact(transaction)).thenThrow(new InvalidAmountException());

        mockMvc.perform(post("/api/v1/transactions")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact(transaction);
    }

    @Test
    void test_fetchAllTransactionsSuccessfully() throws Exception {
        when((transactionService.fetch())).thenReturn(new ResponseEntity<>(HttpStatus.FOUND));

        mockMvc.perform(get("/api/v1/transactions/")).andExpect(status().isFound());
        verify(transactionService, times(1)).fetch();
    }

    @Test
    void test_unknownUserWhileFetchingTransactions_isBadRequest() throws Exception {
        when(transactionService.fetch()).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/v1/transactions/")).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).fetch();
    }
}
