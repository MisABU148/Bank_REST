package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@RequiredArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Firstname is required")
    private String firstName;
    @NotBlank(message = "Lastname is required")
    private String lastName;
    @NotBlank(message = "Age is required")
    private Date age;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany
    @JoinColumn(name = "card_id")
    private List<Card> card;
}
