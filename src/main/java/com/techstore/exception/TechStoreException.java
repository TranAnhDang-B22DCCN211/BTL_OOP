package com.techstore.exception;

public class TechStoreException extends RuntimeException {

    public TechStoreException(String message) {
        super(message);
    }

    public TechStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
