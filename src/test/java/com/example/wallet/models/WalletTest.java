package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.Currency;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class WalletTest {
    @Test
    void test_depositAmountToWallet() {
        User user = mock(User.class);
        Wallet wallet = new Wallet(user);

        wallet.deposit(new Money(25, Currency.INR));

        assertEquals(25, wallet.getMoney().getAmount());
    }

    @Test
    void test_depositInvalidAmountToWallet_throwsException() {
        Wallet wallet = new Wallet();

        assertThrows(InvalidAmountException.class, () -> wallet.deposit(new Money(0, Currency.INR)));
    }

    @Test
    void test_withdrawingAmountFromWallet() {
        User user = mock(User.class);
        Wallet wallet = new Wallet(user);

        wallet.deposit(new Money(100, Currency.INR));
        wallet.withdraw(new Money(15, Currency.INR));

        assertEquals(85, wallet.getMoney().getAmount());
    }

    @Test
    void test_withdrawingInvalidAmount_throwsException() {
        Wallet wallet = new Wallet();

        assertThrows(InvalidAmountException.class, () -> wallet.withdraw(new Money(0, Currency.INR)));
    }

    @Test
    void test_withdrawingMoreThanBalance_throwsException() {
        User user = mock(User.class);
        Wallet wallet = new Wallet(user);
        wallet.deposit(new Money(10, Currency.INR));

        assertThrows(OverWithdrawalException.class, () -> wallet.withdraw(new Money(30, Currency.INR)));
    }
}
