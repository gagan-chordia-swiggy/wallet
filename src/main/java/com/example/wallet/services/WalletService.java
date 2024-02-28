package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.Money;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.enums.Currency;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;

import com.example.wallet.repository.PassbookRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import converter.CurrencyGrpc;
import converter.Request;
import converter.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PassbookRepository passbookRepository;

    public ResponseEntity<ApiResponse> create(User user) {
        Wallet wallet = new Wallet(user);
        walletRepository.save(wallet);
        ApiResponse response = ApiResponse.builder()
                .message("New wallet created")
                .developerMessage("wallet created")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .data(Map.of("wallet", new WalletResponse(wallet)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> create() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return this.create(user);
    }

    public ResponseEntity<ApiResponse> deposit(Long walletId, Money request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        Wallet wallet = walletRepository.findByIdAndUser(walletId, user).orElseThrow(UnauthorizedWalletAccessException::new);
        request = request.convert(wallet.getMoney().getCurrency());
        wallet.deposit(request);

        ApiResponse response = ApiResponse.builder()
                .message("Amount deposited")
                .developerMessage("Amount deposited")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", new WalletResponse(wallet)))
                .build();

        Money depositedMoney = new Money(request.getAmount(), user.getLocation().getCurrency());
        PassbookEntry entry = PassbookEntry.builder()
                .wallet(wallet)
                .money(depositedMoney)
                .type(TransactionType.DEPOSIT)
                .serviceCharge(0.0)
                .build();

        passbookRepository.save(entry);
        walletRepository.save(wallet);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse> withdraw(Long walletId, Money request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new UserNotFoundException();
        }

        Wallet wallet = walletRepository.findByIdAndUser(walletId, user).orElseThrow(UnauthorizedWalletAccessException::new);
        request = request.convert(wallet.getMoney().getCurrency());

        wallet.withdraw(request);

        ApiResponse response = ApiResponse.builder()
                .message("Amount withdrawn")
                .developerMessage("Amount withdrawn")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallet", new WalletResponse(wallet)))
                .build();

        Money withdrawnMoney = new Money(request.getAmount(), user.getLocation().getCurrency());
        PassbookEntry entry = PassbookEntry.builder()
                .wallet(wallet)
                .money(withdrawnMoney)
                .type(TransactionType.WITHDRAW)
                .serviceCharge(0.0)
                .build();

        passbookRepository.save(entry);
        walletRepository.save(wallet);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse> getWallets() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        List<Wallet> wallets = walletRepository.findAllByUser(user);
        List<WalletResponse> walletResponses = new ArrayList<>();

        for (Wallet wallet: wallets) {
            walletResponses.add(new WalletResponse(wallet));
        }

        ApiResponse response = ApiResponse.builder()
                .message("fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("wallets", walletResponses))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
