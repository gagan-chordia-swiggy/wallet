package com.example.wallet.models;

import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
public class Wallet {
    private double balance;

    public void withdraw(double amount) {
        log.info("Wallet --> Withdrawal start");

        if (amount < 0.01) {
            log.error("Wallet --> Invalid Amount");
            throw new InvalidAmountException();
        }

        if (this.balance - amount < 0) {
            log.error("Wallet --> No Balance");
            throw new OverWithdrawalException();
        }

        log.info("Wallet --> Success");
        this.balance -= amount;
    }

    public void deposit(double amount) {
        log.info("Wallet --> Deposit start");
        if (amount < 0.01) {
            log.error("Wallet --> Invalid Amount");
            throw new InvalidAmountException();
        }

        log.info("Wallet --> Success");
        this.balance += amount;
    }
}
