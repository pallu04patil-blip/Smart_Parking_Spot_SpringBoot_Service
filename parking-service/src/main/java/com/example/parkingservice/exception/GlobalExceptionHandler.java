package com.example.parkingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice makes this a global, cross-controller handler:
// any exception thrown inside a @RestController anywhere in the app
// is caught here instead of turning into a raw 500 stack trace.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Triggered automatically when a @Valid-annotated @RequestBody fails
    // its bean-validation checks (e.g. blank spot number, blank zone name).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("details", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Triggered when we look up/update/delete a spot id that doesn't exist.
    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SpotNotFoundException ex) {
        return notFoundBody(ex.getMessage());
    }

    // Triggered when we look up/update/delete a zone id that doesn't exist.
    @ExceptionHandler(ZoneNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ZoneNotFoundException ex) {
        return notFoundBody(ex.getMessage());
    }

    // Triggered by BookingController via ParkingSpotService when a booking
    // action conflicts with the spot's current state (booking an already-booked
    // spot, or releasing one that isn't booked). The request itself is well-formed,
    // it just clashes with the resource's current state — hence 409, not 400.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> notFoundBody(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", message);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
