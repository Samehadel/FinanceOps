package com.financeops.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Error> status(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new Error(exception.getReason()));
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
