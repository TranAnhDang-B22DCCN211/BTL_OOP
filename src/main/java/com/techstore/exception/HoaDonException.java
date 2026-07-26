package com.techstore.exception;

public class HoaDonException extends TechStoreException {
    public HoaDonException(String message) {
        super(message);
    }

    public HoaDonException(String message, Throwable cause) {
        super(message, cause);
    }
}
