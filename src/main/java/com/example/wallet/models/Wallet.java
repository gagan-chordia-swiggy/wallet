package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Role;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Money money = new Money();

    public void withdraw(Money amount) {
        log.info("Wallet --> Withdrawal start");

        if (amount.getAmount() < 0.01) {
            log.error("Wallet --> Invalid Amount");
            throw new InvalidAmountException();
        }

        if (this.money.getAmount() - amount.getAmount() < 0) {
            log.error("Wallet --> No Balance");
            throw new OverWithdrawalException();
        }

        log.info("Wallet --> Success");
        this.money.setAmount(this.money.getAmount() - amount.getAmount());
    }

    public void deposit(Money amount) {
        log.info("Wallet --> Deposit start");
        if (amount.getAmount() < 0.01) {
            log.error("Wallet --> Invalid Amount");
            throw new InvalidAmountException();
        }

        log.info("Wallet --> Success");
        this.money.setAmount(this.money.getAmount() + amount.getAmount());
        this.money.setCurrency(amount.getCurrency());
    }
}
