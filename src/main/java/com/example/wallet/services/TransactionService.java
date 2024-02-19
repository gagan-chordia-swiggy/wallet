package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse> transact(String receiver, Money request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new UserNotFoundException();
        }

        User anotherUser = userRepository.findByUsername(receiver).orElseThrow(UserNotFoundException::new);

        Wallet usersWallet = user.getWallet();
        Wallet anotherUsersWallet = anotherUser.getWallet();

        usersWallet.withdraw(request);
        anotherUsersWallet.deposit(request);

        walletRepository.saveAll(List.of(usersWallet, anotherUsersWallet));

        ApiResponse response = ApiResponse.builder()
                .message("Transaction complete")
                .developerMessage("transaction complete")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}
