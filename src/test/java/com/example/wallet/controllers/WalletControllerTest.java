package com.example.wallet.controllers;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.services.WalletService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Mock
    private WalletService walletService;

   @InjectMocks
   private WalletController walletController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
       openMocks(this);
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_walletIsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());

       walletController.create();
        verify(walletService, times(1)).create();
    }

    @Test
    void test_walletIsNotCreatedWithUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_amountDeposited() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        Money money = new Money(10, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());

       walletController.deposit(1L, money);
        verify(walletService, times(1)).deposit(1L, money);
    }

    @Test
    void test_amountDepositedWithUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
        Money money = new Money(10, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_invalidAmountDeposited_throwsException() throws Exception {
        Money money = new Money(0, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_amountWithdrawn() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        Money money = new Money(5, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        this.mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());

       walletController.withdraw(1L, money);
        verify(walletService, times(1)).withdraw(1L, money);
    }

    @Test
    void test_amountWithdrawnWithUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
        Money money = new Money(5, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        this.mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_invalidAmountWithdrawn_throwsException() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        Money money = new Money(0, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_OverWithdrawal_throwsException() throws Exception {
        Money money = new Money(15, Currency.INR);
        String request = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void test_FetchAllWalletsSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/wallets")).andExpect(status().isFound());

       walletController.getWallets();
        verify(walletService, times(1)).getWallets();
    }

    @Test
    void test_FetchAllWalletsWithUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/wallets")).andExpect(status().isUnauthorized());
    }
}
