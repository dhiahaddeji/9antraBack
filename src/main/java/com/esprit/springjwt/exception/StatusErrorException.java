package com.esprit.springjwt.exception;

public class StatusErrorException extends RuntimeException{
    public StatusErrorException() {
        super("Status not modified");
    }

    public StatusErrorException(String message) {
        super(message);
    }

    public StatusErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
