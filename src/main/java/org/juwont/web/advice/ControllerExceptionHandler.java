package org.juwont.web.advice;

import org.juwont.repository.exception.AccountNotFoundException;
import org.juwont.service.exception.AccountInArrearsException;
import org.juwont.service.exception.InvalidFundAmountException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(final RuntimeException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AccountInArrearsException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundExceptionException(final RuntimeException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidFundAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFundAmountExceptionException(final RuntimeException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleFieldValidationError(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getBindingResult().getFieldError());

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse("Error on field (%s) : %s".formatted(fieldError.getField(), fieldError.getDefaultMessage())));
    }

    public record ErrorResponse(String errorMessage) {
    }
}
