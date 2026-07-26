package com.techstore.exception;

public class InputDataException extends TechStoreException {
    public InputDataException(String message) {
        super(message);
    }

    public InputDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
