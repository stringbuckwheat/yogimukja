package com.memil.yogimukja.common.error.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends RuntimeException {
    private String message;

    public RefreshTokenException(String message) {
        this.message = message;
    }
}
