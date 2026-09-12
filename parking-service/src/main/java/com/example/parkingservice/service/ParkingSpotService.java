package com.example.parkingservice.service;

import com.example.parkingservice.exception.SpotNotFoundException;
import com.example.parkingservice.model.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ParkingSpotService {

    // In-memory "database" — thread-safe map keyed by spot id.
    // Good enough for a demo/viva; swap for a JPA repository + real DB later
    // without touching the controllers.
    private final Map<Long, ParkingSpot> spots = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ParkingSpotService() {
        // seed a few spots so GET /api/spots isn't empty on first run
        addSpot(new ParkingSpot(null, "A1", "Zone A", "CAR", true));
        addSpot(new ParkingSpot(null, "A2", "Zone A", "CAR", true));
        addSpot(new ParkingSpot(null, "B1", "Zone B", "BIKE", true));
    }

    // --- CRUD ---

    public ParkingSpot addSpot(ParkingSpot spot) {
        long id = idCounter.getAndIncrement();
        spot.setId(id);
        spot.setAvailable(true); // every new spot starts out free
        spots.put(id, spot);
        return spot;
    }

    public List<ParkingSpot> getAllSpots() {
        return List.copyOf(spots.values());
    }

    public ParkingSpot getSpotById(Long id) {
        ParkingSpot spot = spots.get(id);
        if (spot == null) {
            throw new SpotNotFoundException(id);
        }
        return spot;
    }

    // Updates the spot's descriptive details (number/zone/vehicle type).
    // Deliberately does NOT touch `available` — that's managed only through
    // bookSpot()/releaseSpot() below so it can't drift out of sync.
    public ParkingSpot updateSpot(Long id, ParkingSpot updatedDetails) {
        ParkingSpot existing = getSpotById(id); // throws if missing
        existing.setSpotNumber(updatedDetails.getSpotNumber());
        existing.setZone(updatedDetails.getZone());
        existing.setVehicleType(updatedDetails.getVehicleType());
        return existing;
    }

    public void deleteSpot(Long id) {
        if (!spots.containsKey(id)) {
            throw new SpotNotFoundException(id);
        }
        spots.remove(id);
    }

    // --- Booking logic (used by BookingController) ---

    public ParkingSpot bookSpot(Long id) {
        ParkingSpot spot = getSpotById(id);
        if (!spot.isAvailable()) {
            throw new IllegalStateException("Spot " + spot.getSpotNumber() + " is already booked");
        }
        spot.setAvailable(false);
        return spot;
    }

    public ParkingSpot releaseSpot(Long id) {
        ParkingSpot spot = getSpotById(id);
        if (spot.isAvailable()) {
            throw new IllegalStateException("Spot " + spot.getSpotNumber() + " is not currently booked");
        }
        spot.setAvailable(true);
        return spot;
    }

    public List<ParkingSpot> getAvailableSpots() {
        return spots.values().stream()
                .filter(ParkingSpot::isAvailable)
                .toList();
    }
}
