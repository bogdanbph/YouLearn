package com.youlearn.youlearn.exception.config;

import com.youlearn.youlearn.dto.ErrorResponseDto;
import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.exception.NotFoundException;
import com.youlearn.youlearn.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(BadRequestException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getErrorCode(),
                exception.getMessage());
    }

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
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponse(Throwable throwable, HttpStatus status, String errorCode, String errorMessage) {
        log.warn(throwable.getMessage(), throwable);

        ErrorResponseDto dto = new ErrorResponseDto();
        dto.setStatus(status.value());
        dto.setCode(errorCode);
        dto.setMessage(errorMessage);
        return ResponseEntity.status(status.value()).body(dto);
    }

}
