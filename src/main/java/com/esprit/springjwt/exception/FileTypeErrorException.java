package com.esprit.springjwt.exception;

public class FileTypeErrorException extends RuntimeException {
    public FileTypeErrorException() {
        super();
    }

    public FileTypeErrorException(String message) {
        super(message);
    }

    public FileTypeErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
