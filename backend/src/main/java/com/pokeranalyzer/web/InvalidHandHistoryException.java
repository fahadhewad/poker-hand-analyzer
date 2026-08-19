package com.pokeranalyzer.web;

public class InvalidHandHistoryException extends RuntimeException {

    public InvalidHandHistoryException(String message) {
        super(message);
    }

    public InvalidHandHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
