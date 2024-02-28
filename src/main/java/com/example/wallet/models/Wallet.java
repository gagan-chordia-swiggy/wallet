package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Money money;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    public Wallet(User user) {
        this.money = new Money(user.getLocation().getCurrency());
        this.user = user;
    }

    public Wallet(Money money, User user) {
        this.money = money;
        this.user = user;
    }

    public void withdraw(Money amount) {
        this.money.subtract(amount);
    }

    public void deposit(Money amount) {
        this.money.add(amount);
    }
}
