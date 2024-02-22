package com.example.wallet.repository;

import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByIdAndUser(Long id, User user);
}
