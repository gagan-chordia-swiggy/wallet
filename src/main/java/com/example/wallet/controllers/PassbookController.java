package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.services.PassbookEntryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/entries")
public class PassbookController {
    private final PassbookEntryService passbookEntryService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> fetchAll() {
        return this.passbookEntryService.fetchAll();
    }
}
