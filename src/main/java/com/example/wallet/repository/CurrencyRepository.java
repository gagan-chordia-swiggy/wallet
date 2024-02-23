package com.example.wallet.repository;

import com.example.wallet.enums.Currency;
import com.example.wallet.models.CurrencyValue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyValue, Currency> {
}
