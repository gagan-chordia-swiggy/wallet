package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {
    @Test
    void test_depositAmountToWallet() {
        Wallet wallet = new Wallet();

        wallet.deposit(new Money(25));

        assertEquals(25, wallet.getMoney().getAmount());
    }

    @Test
    void test_depositInvalidAmountToWallet_throwsException() {
        Wallet wallet = new Wallet();

        assertThrows(InvalidAmountException.class, () -> wallet.deposit(new Money(0)));
    }

    @Test
    void test_withdrawingAmountFromWallet() {
        Wallet wallet = new Wallet();

        wallet.deposit(new Money(100));
        wallet.withdraw(new Money(15));

        assertEquals(85, wallet.getMoney().getAmount());
    }

    @Test
    void test_withdrawingInvalidAmount_throwsException() {
        Wallet wallet = new Wallet();

        assertThrows(InvalidAmountException.class, () -> wallet.withdraw(new Money(0)));
    }

    @Test
    void test_withdrawingMoreThanBalance_throwsException() {
        Wallet wallet = new Wallet();
        wallet.deposit(new Money(10));

        assertThrows(OverWithdrawalException.class, () -> wallet.withdraw(new Money(30)));
    }
}
