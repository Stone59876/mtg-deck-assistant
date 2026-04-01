package com.clementcogo.mtgdeckassistant.advice;

import com.clementcogo.mtgdeckassistant.exception.ConflictException;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.exception.RateLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
        // Tu récupères le 1er message d'erreur de validation (simple)
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");

        return Map.of(
                "error", "Bad Request",
                "message", message
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // on recupere le 1er message d'erreur
        return Map.of("error", "Bad Request",
                "message", ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public Map<String, String> handleConflictException(ConflictException ex) {
        return Map.of("error","Conflict",
                "message", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RateLimitException.class)
    public Map<String, String> handleRateLimitException(RateLimitException ex) {
        return Map.of("error","Too Many Requests",
                "message", ex.getMessage());
    }

}
