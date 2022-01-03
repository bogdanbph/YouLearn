package com.youlearn.youlearn.exception;

import com.youlearn.youlearn.exception.config.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseException {

    public BadRequestException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BadRequestException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

}
