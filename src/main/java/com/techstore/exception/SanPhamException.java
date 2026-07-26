package com.techstore.exception;

public class SanPhamException extends TechStoreException {
    public SanPhamException(String message) {
        super(message);
    }

    public SanPhamException(String message, Throwable cause) {
        super(message, cause);
    }
}
