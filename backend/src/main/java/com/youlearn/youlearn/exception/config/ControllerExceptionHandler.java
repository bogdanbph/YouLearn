package com.youlearn.youlearn.exception.config;

import com.youlearn.youlearn.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequest(BaseException exception) {
        log.error(exception.getMessage(), exception);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(exception.getMessage(),
                exception.getHttpStatus(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, exception.getHttpStatus());
    }
/*
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException exception) {
        return createErrorResponse(exception, HttpStatus.UNAUTHORIZED, exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException exception) {
        return createErrorResponse(exception, HttpStatus.NOT_FOUND, exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleValidationException(Exception exception) {
        List<ObjectError> allErrors = ((MethodArgumentNotValidException) exception).getBindingResult()
                .getAllErrors();
        String validationErrors = allErrors
                .stream()
                .map(e -> ((FieldError) e).getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST, validationErrors,
                "Request body validation errors");
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST, "invalid.request.param",
                "Request body validation errors");
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDto> handleException(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);

        return createErrorResponse(throwable, HttpStatus.INTERNAL_SERVER_ERROR, "internal.server.error",
                "Internal Server Error");
    }*/

}
