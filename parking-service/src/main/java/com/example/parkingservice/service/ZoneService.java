package com.example.parkingservice.service;

import com.example.parkingservice.exception.ZoneNotFoundException;
import com.example.parkingservice.model.Zone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ZoneService {

    private final Map<Long, Zone> zones = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ZoneService() {
        // seed a couple so GET /api/zones isn't empty on first run
        addZone(new Zone(null, "Zone A", "Ground floor, near main entrance"));
        addZone(new Zone(null, "Zone B", "Basement level, two-wheelers"));
    }

    public Zone addZone(Zone zone) {
        long id = idCounter.getAndIncrement();
        zone.setId(id);
        zones.put(id, zone);
        return zone;
    }

    public List<Zone> getAllZones() {
        return List.copyOf(zones.values());
    }

    public Zone getZoneById(Long id) {
        Zone zone = zones.get(id);
        if (zone == null) {
            throw new ZoneNotFoundException(id);
        }
        return zone;
    }

    public Zone updateZone(Long id, Zone updatedDetails) {
        Zone existing = getZoneById(id); // throws if missing
        existing.setName(updatedDetails.getName());
        existing.setDescription(updatedDetails.getDescription());
        return existing;
    }

    public void deleteZone(Long id) {
        if (!zones.containsKey(id)) {
            throw new ZoneNotFoundException(id);
        }
        zones.remove(id);
    }
}
