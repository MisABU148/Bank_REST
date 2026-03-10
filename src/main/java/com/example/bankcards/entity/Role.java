package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN,
    USER;

    @JsonCreator
    public static Role fromString(String key) {
        return key == null ? null : Role.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
