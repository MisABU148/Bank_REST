package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.security.JwtAuthenticationDto;
import com.example.bankcards.dto.security.RefreshTokenDto;
import com.example.bankcards.dto.security.UserCredentialsDto;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> signIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        try {
            JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userCredentialsDto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authetication failed " + e.getMessage());
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDto> signUp(@Valid @RequestBody UserDto userDto) {
        JwtAuthenticationDto jwtAuthenticationDto = userService.signUp(userDto);
        return ResponseEntity.ok(jwtAuthenticationDto);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }
}
