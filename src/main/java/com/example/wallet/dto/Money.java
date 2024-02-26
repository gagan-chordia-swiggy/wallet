package com.example.wallet.dto;

import com.example.wallet.enums.Currency;
import jakarta.persistence.Embeddable;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
