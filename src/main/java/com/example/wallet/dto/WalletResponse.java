package com.example.wallet.dto;

import com.example.wallet.models.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WalletResponse {
    private Money money;

    public WalletResponse(Wallet wallet) {
        this.money = wallet.getMoney();
    }
}
