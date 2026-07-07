package com.carwash.carwashsystem.exception;

public class InvalidQRCodeException extends RuntimeException {
    public InvalidQRCodeException(String message) {
        super(message);
    }
}
