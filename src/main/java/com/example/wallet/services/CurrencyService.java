package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.CurrencyDTO;
import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.CurrencyAlreadyExistsException;
import com.example.wallet.models.CurrencyValue;
import com.example.wallet.repository.CurrencyRepository;
import com.example.wallet.exceptions.CurrencyNotFoundException;

import converter.CurrencyGrpc;
import converter.Request;
import converter.Response;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${converter.grpc.service.host}")
    private String host;

    @Value("${converter.grpc.service.port}")
    private int port;

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

    public double convert(Currency from, Currency to, double value) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        CurrencyGrpc.CurrencyBlockingStub stub = CurrencyGrpc.newBlockingStub(channel);

        Request request = Request.newBuilder()
                .setFromCurrency(from.toString())
                .setToCurrency(to.toString())
                .setValue((float) value)
                .build();

        Response response = stub.convert(request);
        Money money = new Money(response.getValue(), to);

        channel.shutdown();

        return money.getAmount();
    }
}
