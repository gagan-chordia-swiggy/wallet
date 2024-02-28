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
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Money money;

    private Double serviceCharge;

    private Double conversionValue;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
