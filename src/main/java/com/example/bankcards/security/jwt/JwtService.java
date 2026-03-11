package com.example.bankcards.security.jwt;


import com.example.bankcards.dto.security.JwtAuthenticationDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtService {

    @Value("4uZgYYxaCDKf5MGFV11opfqbpuBzSXVEB0bBHip9194")
    private String jwtSecret;

    private String generateJwtToken(String userName) {
        Date date = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(userName)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String userName) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(userName)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtAuthenticationDto refreshBaseToken(String userName, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(userName));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public JwtAuthenticationDto generateAuthToken(String userName) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(userName));
        jwtDto.setRefreshToken(generateRefreshToken(userName));
        return jwtDto;
    }

    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired exception {}", String.valueOf(e));
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported jwt exception {}", String.valueOf(e));
        } catch (MalformedJwtException e) {
            log.info("Malformed jwt exception {}", String.valueOf(e));
        } catch (SecurityException e) {
            log.info("Security exception {}", String.valueOf(e));
        } catch (Exception e) {
            log.info("invalid tiken {}", String.valueOf(e));
        }
        return false;
    }
}
