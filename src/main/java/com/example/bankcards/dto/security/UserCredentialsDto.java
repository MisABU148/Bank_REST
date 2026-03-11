package com.example.bankcards.dto.security;

import lombok.Data;

@Data
public class UserCredentialsDto {
    private String userName;
    private String password;
}
