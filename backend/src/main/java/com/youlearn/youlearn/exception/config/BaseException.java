package com.youlearn.youlearn.exception.config;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    protected BaseException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    protected BaseException(HttpStatus httpStatus, String message, Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    protected BaseException(Throwable cause) {
        super(cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "Internal Server Error";
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
