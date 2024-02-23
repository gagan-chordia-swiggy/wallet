package com.example.wallet.controllers;

import com.example.wallet.dto.CurrencyDTO;
import com.example.wallet.enums.Currency;
import com.example.wallet.services.CurrencyService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTest {
    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(currencyService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_addCurrencySuccessfully() throws Exception {
        CurrencyDTO dto = new CurrencyDTO(Currency.INR, 30);
        String request = objectMapper.writeValueAsString(dto);

        when(currencyService.add(dto)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/admin/currencies")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isCreated());
        verify(currencyService, times(1)).add(dto);
    }

    @Test
    @WithAnonymousUser
    void test_userAddingCurrency_sayUnauthorized() throws Exception {
        CurrencyDTO dto = new CurrencyDTO(Currency.INR, 30);
        String request = objectMapper.writeValueAsString(dto);

        when(currencyService.add(dto)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/v1/admin/currencies")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_updateSingleCurrencySuccessfully() throws Exception {
        CurrencyDTO dto = new CurrencyDTO(Currency.INR, 30);
        String request = objectMapper.writeValueAsString(dto);

        when(currencyService.update(dto)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(put("/api/v1/admin/currencies/currency")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isOk());
        verify(currencyService, times(1)).update(dto);
    }

    @Test
    @WithAnonymousUser
    void test_userUpdatingCurrency_sayUnauthorized() throws Exception {
        CurrencyDTO dto = new CurrencyDTO(Currency.INR, 30);
        String request = objectMapper.writeValueAsString(dto);

        when(currencyService.update(dto)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(put("/api/v1/admin/currencies/currency")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_updateMultipleCurrencySuccessfully() throws Exception {
        CurrencyDTO firstDto = new CurrencyDTO(Currency.INR, 30);
        CurrencyDTO secondDto = new CurrencyDTO(Currency.GBP, 30);
        List<CurrencyDTO> requestList = List.of(firstDto, secondDto);
        String request = objectMapper.writeValueAsString(requestList);

        when(currencyService.update(requestList)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(put("/api/v1/admin/currencies")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isOk());
        verify(currencyService, times(1)).update(requestList);
    }

    @Test
    @WithAnonymousUser
    void test_userUpdatingMultipleCurrency_sayUnauthorized() throws Exception {
        CurrencyDTO firstDto = new CurrencyDTO(Currency.INR, 30);
        CurrencyDTO secondDto = new CurrencyDTO(Currency.GBP, 30);
        List<CurrencyDTO> requestList = List.of(firstDto, secondDto);
        String request = objectMapper.writeValueAsString(requestList);

        when(currencyService.update(requestList)).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(put("/api/v1/admin/currencies")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isUnauthorized());
    }
}
