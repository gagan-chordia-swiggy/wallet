package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.dto.TransactionResponse;
import com.example.wallet.enums.Currency;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.IncompatibleCurrencyException;
import com.example.wallet.exceptions.TransactionForSameWalletException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.Transaction;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.PassbookRepository;
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

    private final PassbookRepository passbookRepository;

    public ResponseEntity<ApiResponse> transact(TransactionRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User anotherUser = userRepository.findByUsername(request.getReceiver()).orElseThrow(UserNotFoundException::new);
        Wallet usersWallet = walletRepository.findByIdAndUser(request.getSendingWalletId(), user)
                .orElseThrow(UnauthorizedWalletAccessException::new);
        Wallet anotherUsersWallet = walletRepository.findByIdAndUser(request.getReceivingWalletId(), anotherUser)
                .orElseThrow(UnauthorizedWalletAccessException::new);

        isSameWallet(usersWallet, anotherUsersWallet);
        isIncompatibleCurrency(request, usersWallet);

        Money forexMoney = null;
        Money serviceCharge = null;
        Currency anotherUserCurrency = anotherUsersWallet.getMoney().getCurrency();
        if (!anotherUserCurrency.equals(request.getMoney().getCurrency())) {
            serviceCharge = new Money(10.0, Currency.INR);
            serviceCharge = serviceCharge.convert(usersWallet.getMoney().getCurrency());
            forexMoney = request.getMoney().convert(anotherUserCurrency);
        }

        if (serviceCharge != null) {
            usersWallet.withdraw(serviceCharge);
        }

        usersWallet.withdraw(request.getMoney());
        anotherUsersWallet.deposit(forexMoney != null ? forexMoney : request.getMoney());

        Long timestamp = System.currentTimeMillis();
        PassbookEntry senderEntry = PassbookEntry.builder()
                .money(request.getMoney())
                .timestamp(timestamp)
                .wallet(usersWallet)
                .type(TransactionType.TRANSFERRED)
                .serviceCharge(serviceCharge != null ? serviceCharge.getAmount() : 0.0)
                .build();

        PassbookEntry receiverEntry = PassbookEntry.builder()
                .money(forexMoney != null ? forexMoney : request.getMoney())
                .timestamp(timestamp)
                .wallet(usersWallet)
                .type(TransactionType.RECEIVED)
                .serviceCharge(0.0)
                .build();

        Transaction transaction = Transaction.builder()
                .sender(user)
                .receiver(anotherUser)
                .senderEntry(senderEntry)
                .receiverEntry(receiverEntry)
                .build();

        walletRepository.saveAll(List.of(usersWallet, anotherUsersWallet));
        passbookRepository.saveAll(List.of(senderEntry, receiverEntry));
        transactionRepository.save(transaction);

        ApiResponse response = ApiResponse.builder()
                .timestamp(timestamp)
                .message("Transaction complete")
                .developerMessage("transaction complete")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse> fetch() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        List<Transaction> transactions = transactionRepository.findAllBySenderOrReceiver(user, user);
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

    private static void isIncompatibleCurrency(TransactionRequest request, Wallet usersWallet) {
        if (!usersWallet.getMoney().getCurrency().equals(request.getMoney().getCurrency())) {
            throw new IncompatibleCurrencyException();
        }
    }

    private static void isSameWallet(Wallet usersWallet, Wallet anotherUsersWallet) {
        if (usersWallet.equals(anotherUsersWallet)) {
            throw new TransactionForSameWalletException();
        }
    }
}
