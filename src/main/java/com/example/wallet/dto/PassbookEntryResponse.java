package com.example.wallet.dto;

import com.example.wallet.enums.TransactionType;
import com.example.wallet.models.PassbookEntry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassbookEntryResponse {
    private Long id;
    private Long timestamp;
    private String username;
    private Money money;
    private TransactionType type;

    public PassbookEntryResponse(PassbookEntry entry) {
        this.id = entry.getId();
        this.timestamp = entry.getTimestamp();
        this.username = entry.getUser().getUsername();
        this.money = entry.getMoney();
        this.type = entry.getType();
    }
}
