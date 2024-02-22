package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionResponse;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.TransactionForSameUserException;
import com.example.wallet.exceptions.TransactionNotFoundException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.Transaction;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    public ResponseEntity<ApiResponse> transact(String receiver, Money request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new UserNotFoundException();
        }

        User anotherUser = userRepository.findByUsername(receiver).orElseThrow(UserNotFoundException::new);

        if (user.equals(anotherUser)) {
            throw new TransactionForSameUserException();
        }

        Wallet usersWallet = user.getWallet();
        Wallet anotherUsersWallet = anotherUser.getWallet();

        usersWallet.withdraw(request);
        anotherUsersWallet.deposit(request);

        Long timestamp = System.currentTimeMillis();

        Transaction transaction = Transaction.builder()
                .user(user)
                .money(request)
                .type(TransactionType.TRANSFERRED)
                .timestamp(timestamp)
                .build();
        Transaction anotherTransaction = Transaction.builder()
                .user(anotherUser)
                .money(request)
                .type(TransactionType.RECEIVED)
                .timestamp(timestamp)
                .build();

        walletRepository.saveAll(List.of(usersWallet, anotherUsersWallet));
        transactionRepository.saveAll(List.of(transaction, anotherTransaction));

        ApiResponse response = ApiResponse.builder()
                .message("Transaction complete")
                .developerMessage("transaction complete")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse> fetch() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        List<TransactionResponse> responses = new ArrayList<>();

        for (Transaction transaction : transactions) {
            responses.add(toDto(transaction));
        }

        ApiResponse response = ApiResponse.builder()
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("transactions", responses))
                .build();

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    public ResponseEntity<ApiResponse> fetchByTimestamp(Long timestamp) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        Transaction transaction = transactionRepository.findByUserAndTimestamp(user, timestamp)
                .orElseThrow(TransactionNotFoundException::new);

        ApiResponse response = ApiResponse.builder()
                .timestamp(timestamp)
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("transactions", toDto(transaction)))
                .build();

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    // Helper Methods
    private TransactionResponse toDto(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .username(transaction.getUser().getUsername())
                .money(transaction.getMoney())
                .transactionType(transaction.getType())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
