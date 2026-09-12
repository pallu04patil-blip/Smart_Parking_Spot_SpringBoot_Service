package com.example.parkingservice.controller;

import com.example.parkingservice.model.ParkingSpot;
import com.example.parkingservice.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    // POST /api/spots -> add a new spot
    // @Valid triggers bean validation on the incoming JSON body. If it fails,
    // Spring throws MethodArgumentNotValidException -> GlobalExceptionHandler -> 400.
    @PostMapping
    public ResponseEntity<ParkingSpot> addSpot(@Valid @RequestBody ParkingSpot spot) {
        ParkingSpot saved = parkingSpotService.addSpot(spot);
        return new ResponseEntity<>(saved, HttpStatus.CREATED); // 201
    }

    // GET /api/spots -> view all spots
    @GetMapping
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(parkingSpotService.getAllSpots()); // 200
    }

    // GET /api/spots/{id} -> view one spot
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpot> getSpotById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpotService.getSpotById(id));
    }

    // PUT /api/spots/{id} -> update a spot's details (number/zone/vehicle type)
    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpot> updateSpot(@PathVariable Long id, @Valid @RequestBody ParkingSpot spot) {
        return ResponseEntity.ok(parkingSpotService.updateSpot(id, spot));
    }

    // DELETE /api/spots/{id} -> remove a spot entirely
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        parkingSpotService.deleteSpot(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
