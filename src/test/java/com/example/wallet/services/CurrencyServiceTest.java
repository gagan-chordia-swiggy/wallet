package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.CurrencyDTO;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.CurrencyAlreadyExistsException;
import com.example.wallet.models.CurrencyValue;
import com.example.wallet.repository.CurrencyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyServiceTest {
    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_addCurrencySuccessfully() {
        CurrencyDTO request = new CurrencyDTO(Currency.INR, 40);

        when(currencyRepository.existsById(request.getCurrency())).thenReturn(false);
        ResponseEntity<ApiResponse> response = currencyService.add(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("new currency added", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_additionOfDuplicateCurrency_throwsException() {
        CurrencyDTO request = new CurrencyDTO(Currency.INR, 40);

        when(currencyRepository.existsById(request.getCurrency())).thenReturn(true);

        assertThrows(CurrencyAlreadyExistsException.class, () -> {
            ResponseEntity<ApiResponse> response = currencyService.add(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("duplicate currency", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_updateAllCurrenciesSuccessfully() {
        CurrencyDTO firstCurrencyRequest = new CurrencyDTO(Currency.INR, 30);
        CurrencyDTO secondCurrencyRequest = new CurrencyDTO(Currency.USD, 90);
        CurrencyDTO thirdCurrencyRequest = new CurrencyDTO(Currency.GBP, 120);
        CurrencyValue firstCurrency = new CurrencyValue(Currency.INR, 30);
        CurrencyValue secondCurrency = new CurrencyValue(Currency.USD, 40);
        CurrencyValue thirdCurrency = new CurrencyValue(Currency.GBP, 90);
        List<CurrencyDTO> requests = List.of(firstCurrencyRequest, secondCurrencyRequest, thirdCurrencyRequest);
        List<CurrencyValue> currencies = List.of(firstCurrency, secondCurrency, thirdCurrency);

        when(currencyRepository.findById(Currency.INR)).thenReturn(Optional.of(firstCurrency));
        when(currencyRepository.findById(Currency.USD)).thenReturn(Optional.of(secondCurrency));
        when(currencyRepository.findById(Currency.GBP)).thenReturn(Optional.of(thirdCurrency));
        when(currencyRepository.saveAll(currencies)).thenReturn(currencies);
        ResponseEntity<ApiResponse> response = currencyService.update(requests);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("currencies updated", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        assertEquals(firstCurrencyRequest.getValue(), firstCurrency.getValue());
        assertEquals(secondCurrencyRequest.getValue(), secondCurrency.getValue());
        assertEquals(thirdCurrencyRequest.getValue(), thirdCurrency.getValue());
        verify(currencyRepository, times(1)).saveAll(currencies);
    }

    @Test
    void test_updateSingleCurrencySuccessfully() {
        CurrencyDTO request = new CurrencyDTO(Currency.INR, 40);
        CurrencyValue currency = new CurrencyValue(Currency.INR, 30);
        List<CurrencyValue> currencyList = List.of(currency);

        when(currencyRepository.findById(Currency.INR)).thenReturn(Optional.of(currency));
        when(currencyRepository.saveAll(currencyList)).thenReturn(currencyList);
        ResponseEntity<ApiResponse> response = currencyService.update(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("currencies updated", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        assertEquals(request.getValue(), currency.getValue());
        verify(currencyRepository, times(1)).saveAll(currencyList);
    }
}
