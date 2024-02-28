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
    private String sender;
    private String receiver;
    private PassbookEntryResponse senderEntry;
    private PassbookEntryResponse receiverEntry;

    public TransactionResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.sender = transaction.getSender().getUsername();
        this.receiver = transaction.getReceiver().getUsername();
        this.senderEntry = new PassbookEntryResponse(transaction.getSenderEntry());
        this.receiverEntry = new PassbookEntryResponse(transaction.getReceiverEntry());
    }
}
