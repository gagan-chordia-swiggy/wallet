package com.example.wallet.controllers;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import com.example.wallet.exceptions.UserNotFoundException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        String request = mapper.writeValueAsString(money);

        when(transactionService.transact("user", money)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/api/v1/users/transactions/user")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());
        verify(transactionService, times(1)).transact("user", money);
    }

    @Test
    void test_transactionNotCompleteWhenUserNotFound_throwsException() throws Exception {
        Money money = new Money(50, Currency.INR);
        String request = mapper.writeValueAsString(money);

        when(transactionService.transact("user", money)).thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/api/v1/users/transactions/user")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact("user", money);
    }

    @Test
    void test_transactionNotCompleteWhenInsufficientFunds_throwsException() throws Exception {
        Money money = new Money(50, Currency.INR);
        String request = mapper.writeValueAsString(money);

        when(transactionService.transact("user", money)).thenThrow(new OverWithdrawalException());

        mockMvc.perform(patch("/api/v1/users/transactions/user")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact("user", money);
    }

    @Test
    void test_transactionNotCompleteWhenInvalidAmountIsTransacted_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        String request = mapper.writeValueAsString(money);

        when(transactionService.transact("user", money)).thenThrow(new InvalidAmountException());

        mockMvc.perform(patch("/api/v1/users/transactions/user")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(transactionService, times(1)).transact("user", money);
    }
}
