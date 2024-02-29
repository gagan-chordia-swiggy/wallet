package com.example.wallet.repository;

import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassbookEntryRepository extends JpaRepository<PassbookEntry, Long> {
    List<PassbookEntry> findAllByWallet(Wallet wallet);
    Optional<PassbookEntry> findByWalletAndTimestamp(Wallet wallet, Long timestamp);
}
