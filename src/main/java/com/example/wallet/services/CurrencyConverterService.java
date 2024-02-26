package com.example.wallet.services;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import converter.CurrencyGrpc;
import converter.Request;
import converter.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConverterService {
    @Value("${converter.grpc.service.host}")
    private String host;

    @Value("${converter.grpc.service.port}")
    private int port;

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
