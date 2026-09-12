package com.example.parkingservice.controller;

import com.example.parkingservice.model.ParkingSpot;
import com.example.parkingservice.service.ParkingSpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// A third REST service (its own base path, /api/bookings) with no storage of
// its own — it composes ParkingSpotService so `available` lives in exactly
// one place and can never drift out of sync between "spots" and "bookings".
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final ParkingSpotService parkingSpotService;

    public BookingController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    // PUT /api/bookings/{id}/book -> mark a spot as occupied
    // Throws IllegalStateException (-> 409) if it's already booked.
    @PutMapping("/{id}/book")
    public ResponseEntity<ParkingSpot> bookSpot(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpotService.bookSpot(id));
    }

    // PUT /api/bookings/{id}/release -> mark a spot as free again
    // Throws IllegalStateException (-> 409) if it wasn't booked.
    @PutMapping("/{id}/release")
    public ResponseEntity<ParkingSpot> releaseSpot(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpotService.releaseSpot(id));
    }

    // GET /api/bookings/available -> spots currently free
    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots() {
        return ResponseEntity.ok(parkingSpotService.getAvailableSpots());
    }
}
