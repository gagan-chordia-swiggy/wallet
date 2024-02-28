package com.example.wallet.controllers;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.models.User;
import com.example.wallet.services.WalletService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @MockBean
    private WalletService walletService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
       reset(walletService);
    }
    @Test
    @WithMockUser(roles = "USER")
    void test_amountDeposited() throws Exception {
        Money money = new Money(10, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.deposit(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());
        verify(walletService, times(1)).deposit(1L, money);
    }

    @Test
    void test_amountDepositedWithUnauthorizedUser() throws Exception {
        Money money = new Money(10, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.deposit(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_invalidAmountDeposited_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.deposit(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(walletService, times(1)).deposit(1L, money);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_depositInUnauthorizedWallet_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.deposit(1L, money)).thenThrow(new UnauthorizedWalletAccessException());

        mockMvc.perform(post("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
        verify(walletService, times(1)).deposit(1L, money);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_amountWithdrawn() throws Exception {
        Money money = new Money(5, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.withdraw(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        this.mockMvc.perform(post("/api/v1/wallets/1/withdrawal")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());
        verify(walletService, times(1)).withdraw(1L, money);
    }

    @Test
    void test_amountWithdrawnWithUnauthorizedUser() throws Exception {
        Money money = new Money(5, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.withdraw(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        this.mockMvc.perform(post("/api/v1/wallets/1/withdrawal")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_invalidAmountWithdrawn_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.withdraw(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/wallets/1/withdrawal")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());

        verify(walletService, times(1)).withdraw(1L, money);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_OverWithdrawal_throwsException() throws Exception {
        Money money = new Money(15, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        when(walletService.withdraw(1L, money)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/wallets/1/withdrawal")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
        verify(walletService, times(1)).withdraw(1L, money);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_FetchAllWalletsSuccessfully() throws Exception {
        when(walletService.getWallets()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/api/v1/wallets")).andExpect(status().isOk());
        verify(walletService, times(1)).getWallets();
    }

    @Test
    void test_FetchAllWalletsWithUnauthorizedUser() throws Exception {
        when(walletService.getWallets()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(get("/api/v1/wallets")).andExpect(status().isUnauthorized());
    }
}
