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
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Money {
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;
}
