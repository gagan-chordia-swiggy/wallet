package com.example.wallet.controllers;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.services.PassbookEntryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/entries")
public class PassbookEntryController {
    private final PassbookEntryService entryService;

    @GetMapping(params = "walletId")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse> fetch(@RequestParam(value = "walletId") Long walletId) {
        return this.entryService.fetch(walletId);
    }

    @GetMapping(params = {"walletId", "timestamp"})
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse> fetch(@RequestParam(value = "walletId") Long walletId, @RequestParam(value = "timestamp") Long timestamp) {
        return this.entryService.fetch(walletId, timestamp);
    }
}
