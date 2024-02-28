package com.example.wallet.repository;

import com.example.wallet.models.Transaction;

import com.example.wallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySenderOrReceiver(User user1, User user2);
}
