package com.example.wallet.repository;

import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassbookRepository extends JpaRepository<PassbookEntry, Long> {
    List<PassbookEntry> findAllByUser(User user);
}
