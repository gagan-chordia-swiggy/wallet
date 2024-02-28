package com.example.wallet.dto;

import com.example.wallet.enums.TransactionType;
import com.example.wallet.models.PassbookEntry;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassbookEntryResponse {
    private Long timestamp;
    private Long walletId;
    private Money money;
    private Double serviceCharge;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    public PassbookEntryResponse(PassbookEntry entry) {
        this.timestamp = entry.getTimestamp();
        this.walletId = entry.getWallet().getId();
        this.money = entry.getMoney();
        this.type = entry.getType();
        this.serviceCharge = entry.getServiceCharge();
    }
}
