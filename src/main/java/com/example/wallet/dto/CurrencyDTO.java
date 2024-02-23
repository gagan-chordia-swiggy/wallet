package com.example.wallet.dto;

import com.example.wallet.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO {
    private Currency currency;
    private double value;
}
