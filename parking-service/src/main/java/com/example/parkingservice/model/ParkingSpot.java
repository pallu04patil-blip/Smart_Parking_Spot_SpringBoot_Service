package com.example.parkingservice.model;

import jakarta.validation.constraints.NotBlank;

public class ParkingSpot {

    // Server-generated; the client never sends this.
    private Long id;

    @NotBlank(message = "Spot number is required")
    private String spotNumber;

    @NotBlank(message = "Zone is required")
    private String zone;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType; // e.g. CAR, BIKE, EV

    // Server-controlled — never settable directly by the client (see
    // ParkingSpotController). Booking/releasing goes through BookingController
    // so this can never drift out of sync with an actual booking.
    private boolean available;

    public ParkingSpot() {
        // required by Jackson
    }

    public ParkingSpot(Long id, String spotNumber, String zone, String vehicleType, boolean available) {
        this.id = id;
        this.spotNumber = spotNumber;
        this.zone = zone;
        this.vehicleType = vehicleType;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(String spotNumber) {
        this.spotNumber = spotNumber;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
