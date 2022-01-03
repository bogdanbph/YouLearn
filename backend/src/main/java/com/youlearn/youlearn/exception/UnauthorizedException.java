package com.youlearn.youlearn.exception;

import com.youlearn.youlearn.exception.config.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message);
    }

    public UnauthorizedException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
