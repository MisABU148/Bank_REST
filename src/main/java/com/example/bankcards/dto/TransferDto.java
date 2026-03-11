package com.example.bankcards.dto;

import lombok.Data;

@Data
public class TransferDto {
    private Long userId;
    private String cardFrom;
    private String cardTo;
    private Long amount;
}
