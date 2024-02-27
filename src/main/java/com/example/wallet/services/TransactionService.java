package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.dto.TransactionResponse;
import com.example.wallet.enums.Currency;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.IncompatibleCurrencyException;
import com.example.wallet.exceptions.TransactionForSameWalletException;
import com.example.wallet.exceptions.TransactionNotFoundException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
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

    private final CurrencyConverterService converterService;

    public ResponseEntity<ApiResponse> transact(TransactionRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new UserNotFoundException();
        }

        User anotherUser = userRepository.findByUsername(request.getReceiver()).orElseThrow(UserNotFoundException::new);

        Wallet usersWallet = walletRepository.findByIdAndUser(request.getSendingWalletId(), user)
                .orElseThrow(UnauthorizedWalletAccessException::new);
        Wallet anotherUsersWallet = walletRepository.findByIdAndUser(request.getReceivingWalletId(), anotherUser)
                .orElseThrow(UnauthorizedWalletAccessException::new);

        isSameWallet(usersWallet, anotherUsersWallet);
        isIncompatibleCurrency(request, usersWallet);

        Double serviceChargeAmount = null;
        Double conversionValue = null;
        Money forexMoney = null;
        Money serviceCharge = null;

        Currency anotherUserCurrency = anotherUsersWallet.getMoney().getCurrency();

        if (!anotherUserCurrency.equals(request.getMoney().getCurrency())) {
            serviceChargeAmount = 10.0;
            serviceChargeAmount = converterService.convert(Currency.INR, anotherUserCurrency, serviceChargeAmount);
            serviceChargeAmount = Math.round(serviceChargeAmount * 100.0) / 100.0;
            serviceCharge = new Money(serviceChargeAmount, request.getMoney().getCurrency());

            conversionValue = converterService.convert(
                    request.getMoney().getCurrency(),
                    anotherUserCurrency,
                    request.getMoney().getAmount());
            conversionValue = Math.round(conversionValue * 100.0) / 100.0;

            forexMoney = new Money(conversionValue, anotherUsersWallet.getMoney().getCurrency());
        }

        if (serviceCharge != null) {
            usersWallet.withdraw(serviceCharge);
        }

        usersWallet.withdraw(request.getMoney());
        anotherUsersWallet.deposit(forexMoney != null ? forexMoney : request.getMoney());

        Long timestamp = System.currentTimeMillis();
        Transaction transferred = Transaction.builder()
                .user(user)
                .money(request.getMoney())
                .type(TransactionType.TRANSFERRED)
                .timestamp(timestamp)
                .conversionValue(null)
                .serviceCharge(serviceChargeAmount)
                .build();
        Transaction received = Transaction.builder()
                .user(anotherUser)
                .money(forexMoney != null ? forexMoney : request.getMoney())
                .type(TransactionType.RECEIVED)
                .timestamp(timestamp)
                .conversionValue(conversionValue)
                .serviceCharge(null)
                .build();

        walletRepository.saveAll(List.of(usersWallet, anotherUsersWallet));
        transactionRepository.saveAll(List.of(transferred, received));

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
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("transactions", new TransactionResponse(transaction)))
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
