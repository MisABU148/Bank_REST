package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "cards")
@Data
@RequiredArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNumber;
    private Date validityPeriod;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long balance;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
