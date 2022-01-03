package com.youlearn.youlearn.exception.config;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String message;

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public BaseException(String errorCode, String message, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
        this.errorCode = "internal.server.error";
        this.message = "Internal Server Error";
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
