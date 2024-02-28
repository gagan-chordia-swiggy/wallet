package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passbook")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassbookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private Money money;

    private Double serviceCharge;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
