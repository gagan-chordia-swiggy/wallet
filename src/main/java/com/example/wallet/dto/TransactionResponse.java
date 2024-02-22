package com.example.wallet.dto;

import com.example.wallet.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String username;
    private Long timestamp;
    private Money money;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
