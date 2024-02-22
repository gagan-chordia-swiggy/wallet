package com.example.wallet.enums;

import lombok.Getter;

@Getter
public enum Location {
    INDIA(Currency.INR),
    UNITED_STATES(Currency.USD),
    BRITAIN(Currency.GBP);

    private final Currency currency;

    Location(Currency currency) {
        this.currency = currency;
    }
}
