package com.example.parkingservice.controller;

import com.example.parkingservice.model.Zone;
import com.example.parkingservice.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    // POST /api/zones -> add a new zone
    @PostMapping
    public ResponseEntity<Zone> addZone(@Valid @RequestBody Zone zone) {
        Zone saved = zoneService.addZone(zone);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // GET /api/zones -> view all zones
    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    // GET /api/zones/{id} -> view one zone
    @GetMapping("/{id}")
    public ResponseEntity<Zone> getZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    // PUT /api/zones/{id} -> update a zone's name/description
    @PutMapping("/{id}")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @Valid @RequestBody Zone zone) {
        return ResponseEntity.ok(zoneService.updateZone(id, zone));
    }

    // DELETE /api/zones/{id} -> remove a zone entirely
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
