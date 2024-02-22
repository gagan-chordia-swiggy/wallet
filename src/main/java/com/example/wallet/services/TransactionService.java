package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.dto.TransactionResponse;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.TransactionForSameUserException;
import com.example.wallet.exceptions.TransactionNotFoundException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.exceptions.WalletNotFoundException;
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

    public ResponseEntity<ApiResponse> transact(TransactionRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new UserNotFoundException();
        }

        User anotherUser = userRepository.findByUsername(request.getReceiver()).orElseThrow(UserNotFoundException::new);

        Wallet usersWallet = walletRepository.findByIdAndUser(request.getSendingWalletId(), user)
                .orElseThrow(WalletNotFoundException::new);
        Wallet anotherUsersWallet = walletRepository.findByIdAndUser(request.getReceivingWalletId(), user)
                .orElseThrow(WalletNotFoundException::new);

        usersWallet.withdraw(request.getMoney());
        anotherUsersWallet.deposit(request.getMoney());

        Long timestamp = System.currentTimeMillis();

        Transaction transaction = Transaction.builder()
                .user(user)
                .money(request.getMoney())
                .type(TransactionType.TRANSFERRED)
                .timestamp(timestamp)
                .build();
        Transaction anotherTransaction = Transaction.builder()
                .user(anotherUser)
                .money(request.getMoney())
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
            responses.add(new TransactionResponse(transaction));
        }

        ApiResponse response = ApiResponse.builder()
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("transactions", responses))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
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
                .data(Map.of("transactions", new TransactionResponse(transaction)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
