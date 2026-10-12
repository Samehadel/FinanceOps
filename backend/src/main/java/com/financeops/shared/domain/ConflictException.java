package com.financeops.shared.domain;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
