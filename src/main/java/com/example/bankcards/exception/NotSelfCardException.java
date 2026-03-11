package com.example.bankcards.exception;

public class NotSelfCardException extends RuntimeException {
    public NotSelfCardException(String message) {
        super(message);
    }
}
