package com.example.wallet.models;

import com.example.wallet.dto.Money;
import com.example.wallet.enums.TransactionType;

import jakarta.persistence.*;

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

    private Long timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Money money;

    @Column
    private Double serviceCharge;

    @Column
    private Double conversionValue;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
