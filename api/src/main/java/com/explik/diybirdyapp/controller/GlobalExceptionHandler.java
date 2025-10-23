package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1️⃣ Handles @Valid DTO errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        for (ObjectError globalError : ex.getBindingResult().getGlobalErrors()) {
            fields.put("_form", globalError.getDefaultMessage());
        }

        return buildValidationErrorResponse(fields);
    }

    // 2️⃣ Handles service-level validation errors
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleServiceValidation(ValidationException ex) {
        return buildValidationErrorResponse(ex.getFields());
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(Map<String, String> fields) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "validation-error");
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }
}

