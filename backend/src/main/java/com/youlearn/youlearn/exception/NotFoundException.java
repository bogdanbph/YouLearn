package com.youlearn.youlearn.exception;

import com.youlearn.youlearn.exception.config.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {

    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

    public NotFoundException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}