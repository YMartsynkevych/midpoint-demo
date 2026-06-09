package com.midpoint.demo.exception;

public class MidPointException extends RuntimeException {
    public MidPointException(String message) {
        super(message);
    }
    public MidPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
