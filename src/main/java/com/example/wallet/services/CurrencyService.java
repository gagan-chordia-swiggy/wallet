package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.CurrencyDTO;
import com.example.wallet.exceptions.CurrencyAlreadyExistsException;
import com.example.wallet.models.CurrencyValue;
import com.example.wallet.repository.CurrencyRepository;
import com.example.wallet.exceptions.CurrencyNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public ResponseEntity<ApiResponse> add(CurrencyDTO request) {
        if (currencyRepository.existsById(request.getCurrency())) {
            throw new CurrencyAlreadyExistsException();
        }

        CurrencyValue currency = new CurrencyValue(request.getCurrency(), request.getValue());
        currencyRepository.save(currency);

        ApiResponse response = ApiResponse.builder()
                .message("Currency added to the database")
                .developerMessage("new currency added")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .data(Map.of("currency", request))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> update(List<CurrencyDTO> requests) {
        List<CurrencyValue> currencies = new ArrayList<>();

        for (CurrencyDTO request : requests) {
            CurrencyValue currencyValue = currencyRepository.findById(request.getCurrency())
                    .orElseThrow(CurrencyNotFoundException::new);
            currencyValue.setValue(request.getValue());
            currencies.add(currencyValue);
        }

        currencyRepository.saveAll(currencies);

        ApiResponse response = ApiResponse.builder()
                .message("All currencies updated")
                .developerMessage("currencies updated")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("currencies", requests))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> update(CurrencyDTO request) {
        return this.update(List.of(request));
    }
}
