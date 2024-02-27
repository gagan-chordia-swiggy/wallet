package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.PassbookEntryResponse;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.User;
import com.example.wallet.repository.PassbookRepository;

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
    private final PassbookRepository passbookRepository;

    public ResponseEntity<ApiResponse> fetchAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UserNotFoundException();
        }

        List<PassbookEntry> entries = passbookRepository.findAllByUser(user);
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
}
