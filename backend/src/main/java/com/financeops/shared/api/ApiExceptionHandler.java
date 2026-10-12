package com.financeops.shared.api;

import com.financeops.shared.domain.RecordNotFoundException;
import com.financeops.shared.domain.InvalidRequestException;
import com.financeops.shared.domain.ConflictException;
import com.financeops.shared.domain.ServiceUnavailableException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ServiceUnavailableException.class)
    ResponseEntity<Error> unavailable(ServiceUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Error(exception.getMessage()));
    }

    @ExceptionHandler(RecordNotFoundException.class)
    ResponseEntity<Error> notFound(RecordNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(exception.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<Error> invalid(InvalidRequestException exception) {
        return ResponseEntity.badRequest().body(new Error(exception.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<Error> conflict(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Error> validation(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(new Error("Check your name and email. Password must contain 10–64 characters."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Error> conflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error("Account could not be created. Try a different email."));
    }

    record Error(String message) {
    }
}
