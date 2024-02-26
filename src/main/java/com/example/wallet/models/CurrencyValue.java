package com.example.wallet.models;

import com.example.wallet.enums.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currencies")
public class CurrencyValue {
    @Id
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private double value;
}
