package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "Firstname is required")
    private String userName;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Age is required")
    @Past(message = "Age must be in the past")
    private Date age;

    @NotNull(message = "Role is required")
    @Pattern(
            regexp = "admin|user",
            message = "Role must be admin or user"
    )
    private String role;

}