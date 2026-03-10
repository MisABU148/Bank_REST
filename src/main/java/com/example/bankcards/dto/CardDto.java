package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Date;

@Data
public class CardDto {

    private Long id;

    @NotNull(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotNull(message = "Validity period is required")
    private Date validityPeriod;

    @NotNull(message = "Status is required")
    private String status;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance cannot be negative")
    private Long balance;

    @NotNull(message = "User ID is required")
    private Long userId;
}