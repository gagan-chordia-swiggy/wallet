package com.example.wallet.dto;

import com.example.wallet.enums.TransactionType;
import com.example.wallet.models.PassbookEntry;
import com.example.wallet.models.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private Long transactionId;
    private String username;
    private Long timestamp;
    private Money money;
    private Double serviceCharge;
    private Double conversionValue;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public TransactionResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.username = transaction.getUser().getUsername();
        this.timestamp = transaction.getTimestamp();
        this.money = transaction.getMoney();
        this.transactionType = transaction.getType();
        this.serviceCharge = transaction.getServiceCharge();
        this.conversionValue = transaction.getConversionValue();
    }

    public TransactionResponse(PassbookEntry passbookEntry) {
        this.transactionId = passbookEntry.getId();
        this.username = passbookEntry.getUser().getUsername();
        this.timestamp = passbookEntry.getTimestamp();
        this.money = passbookEntry.getMoney();
        this.transactionType = passbookEntry.getType();
    }
}
