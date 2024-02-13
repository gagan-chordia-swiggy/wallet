package com.example.wallet.services;

import com.example.wallet.dto.MoneyRequest;
import com.example.wallet.exceptions.InvalidAmountException;
import com.example.wallet.exceptions.OverWithdrawalException;
import com.example.wallet.models.Wallet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletServiceTest {
    @Test
    void test_amountDeposited() {
        Wallet wallet = new Wallet();
        WalletService walletService = new WalletService(wallet);

        walletService.deposit(new MoneyRequest(15));

        assertEquals(15, wallet.getBalance());
    }

    @Test
    void test_invalidAmountDeposited_throwsException() {
        WalletService walletService = new WalletService(new Wallet());

        assertThrows(InvalidAmountException.class, () -> walletService.deposit(new MoneyRequest(-2)));
    }

    @Test
    void test_amountWithdrawn() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        walletService.withdraw(new MoneyRequest(10));

        assertEquals(20, wallet.getBalance());
    }

    @Test
    void test_invalidAmountWithdrawn_throwsException() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        assertThrows(InvalidAmountException.class, () -> walletService.withdraw(new MoneyRequest(-1)));
    }

    @Test
    void test_OverWithdrawal_throwsException() {
        Wallet wallet = new Wallet(30);
        WalletService walletService = new WalletService(wallet);

        assertThrows(OverWithdrawalException.class, () -> walletService.withdraw(new MoneyRequest(40)));
    }
}
