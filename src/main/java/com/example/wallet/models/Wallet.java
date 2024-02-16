package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.*;
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Wallet(Money money) {
        this.money = money;
    }

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
