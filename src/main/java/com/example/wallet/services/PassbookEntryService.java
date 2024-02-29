package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.PassbookEntryResponse;
import com.example.wallet.exceptions.PassbookEntryNotFoundException;
import com.example.wallet.exceptions.UnauthorizedWalletAccessException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import com.example.wallet.models.Wallet;
import com.example.wallet.repository.PassbookEntryRepository;
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
public class PassbookEntryService {
    private final PassbookEntryRepository passbookEntryRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public ResponseEntity<ApiResponse> fetch(Long walletId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Wallet wallet = walletRepository.findByIdAndUser(walletId, user)
                .orElseThrow(UnauthorizedWalletAccessException::new);

        List<PassbookEntry> entries = passbookEntryRepository.findAllByWallet(wallet);
        List<PassbookEntryResponse> responses = new ArrayList<>();

        for (PassbookEntry entry : entries) {
            responses.add(new PassbookEntryResponse(entry));
        }

        ApiResponse response = ApiResponse.builder()
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("entries", responses))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> fetchByTimestamp(Long walletId, Long timestamp) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Wallet wallet = walletRepository.findByIdAndUser(walletId, user)
                .orElseThrow(UnauthorizedWalletAccessException::new);

        PassbookEntry entry = passbookEntryRepository.findByWalletAndTimestamp(wallet, timestamp)
                .orElseThrow(PassbookEntryNotFoundException::new);

        ApiResponse response = ApiResponse.builder()
                .message("Fetched")
                .developerMessage("fetched")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("entry", new PassbookEntryResponse(entry)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
