package com.example.wallet.dto;

import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import converter.CurrencyGrpc;
import converter.Request;
import converter.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.persistence.Embeddable;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Embeddable
public class Money {
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    public Money() {
        this.amount = 0;
        this.currency = Currency.INR;
    }

    public Money(Currency currency) {
        this.amount = 0;
        this.currency = currency;
    }

    public double getAmount() {
        return Math.round(this.amount * 100.0) / 100.0;
    }

    public void add(Money money) {
        money.isInvalidAmount();

        money = money.convert(this.currency);
        this.amount += money.amount;
    }

    public void subtract(Money money) {
        money.isInvalidAmount();
        money = money.convert(this.currency);
        if (this.amount - money.amount < 0) {
            throw new OverWithdrawalException();
        }
        this.amount -= money.amount;
    }

    public Money convert(Currency to) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8001)
                .usePlaintext()
                .build();

        CurrencyGrpc.CurrencyBlockingStub stub = CurrencyGrpc.newBlockingStub(channel);

        Request request = Request.newBuilder()
                .setFromCurrency(this.getCurrency().toString())
                .setToCurrency(to.toString())
                .setValue((float) this.getAmount())
                .build();

        Response response = stub.convert(request);
        Money convertedMoney = new Money(response.getValue(), to);

        channel.shutdown();

        return convertedMoney;
    }

    private void isInvalidAmount() {
        if (this.amount < 0.01) {
            throw new InvalidAmountException();
        }
    }
}
