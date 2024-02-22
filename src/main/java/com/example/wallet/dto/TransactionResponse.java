package com.example.wallet.dto;

import com.example.wallet.enums.TransactionType;
import com.example.wallet.models.Transaction;
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
public class TransactionResponse {
    private Long transactionId;
    private String username;
    private Long timestamp;
    private Money money;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public TransactionResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.username = transaction.getUser().getUsername();
        this.timestamp = transaction.getTimestamp();
        this.money = transaction.getMoney();
        this.transactionType = transaction.getType();
    }
}
