package com.example.parkingservice.exception;

public class SpotNotFoundException extends RuntimeException {
    public SpotNotFoundException(Long id) {
        super("Parking spot not found with id: " + id);
    }
}
