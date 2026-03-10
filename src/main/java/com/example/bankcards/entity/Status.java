package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Status {
    ACTIVE,
    BLOCKED,
    EXPIRED;

    @JsonCreator
    public static Status fromString(String key) {
        log.info("start convert to status, key {}", key);
        return key == null ? null : Status.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
