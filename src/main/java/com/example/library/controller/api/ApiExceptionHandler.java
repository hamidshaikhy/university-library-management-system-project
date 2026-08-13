package com.example.library.controller.api;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.library.controller.api.ApiDtos.ErrorResponse;

@RestControllerAdvice(basePackages = "com.example.library.controller.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse("اطلاعات فرم را بررسی کنید.", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> unreadable() {
        return ResponseEntity.badRequest().body(new ErrorResponse("فرمت اطلاعات ارسالی صحیح نیست.", Map.of()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> status(ResponseStatusException exception) {
        String message = exception.getReason() == null ? "درخواست قابل انجام نیست." : exception.getReason();
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ErrorResponse(message, Map.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> notFound(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage(), Map.of()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> businessRule(IllegalStateException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage(), Map.of()));
    }
}
