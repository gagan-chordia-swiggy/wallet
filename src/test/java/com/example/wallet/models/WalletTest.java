package com.example.wallet.models;

import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {
    @Test
    void test_depositAmountToWallet() {
        Wallet wallet = new Wallet();

        wallet.deposit(25.0);

        assertEquals(25, wallet.getBalance());
    }

    @Test
    void test_depositInvalidAmountToWallet_throwsException() {
        Wallet wallet = new Wallet();

        assertThrows(InvalidAmountException.class, () -> wallet.deposit(0));
    }

    @Test
    void test_withdrawingAmountFromWallet() {
        Wallet wallet = new Wallet(100);

        wallet.withdraw(15);

        assertEquals(85, wallet.getBalance());
    }

    @Test
    void test_withdrawingInvalidAmount_throwsException() {
        Wallet wallet = new Wallet(10);

        assertThrows(InvalidAmountException.class, () -> wallet.withdraw(0));
    }

    @Test
    void test_withdrawingMoreThanBalance_throwsException() {
        Wallet wallet = new Wallet(20);

        assertThrows(OverWithdrawalException.class, () -> wallet.withdraw(30));
    }
}
