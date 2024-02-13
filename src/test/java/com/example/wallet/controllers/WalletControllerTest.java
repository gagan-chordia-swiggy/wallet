package com.example.wallet.controllers;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_walletIsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
    }

    @Test
    void test_amountDeposited() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        String amount = "{\"amount\" : 10.0}";
        mockMvc.perform(patch("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(amount)
        ).andExpect(status().isOk());
    }

    @Test
    void test_invalidAmountDeposited_throwsException() throws Exception {
        String amount = "{\"amount\" : 0}";
        mockMvc.perform(patch("/api/v1/wallets/1/deposit")
                .contentType("application/json")
                .content(amount)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void test_amountWithdrawn() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        String amount = "{\"amount\" : 5.0}";
        this.mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(amount)
        ).andExpect(status().isOk());
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() throws Exception {
        mockMvc.perform(post("/api/v1/wallets")).andExpect(status().isCreated());
        String amount = "{\"amount\" : 0}";
        mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(amount)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void test_OverWithdrawal_throwsException() throws Exception {
        String amount = "{\"amount\" : 15}";
        mockMvc.perform(patch("/api/v1/wallets/1/withdraw")
                .contentType("application/json")
                .content(amount)
        ).andExpect(status().isBadRequest());
    }
}
