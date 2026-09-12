package com.example.parkingservice.model;

import jakarta.validation.constraints.NotBlank;

public class Zone {

    private Long id;

    @NotBlank(message = "Zone name is required")
    private String name;

    private String description;

    public Zone() {
        // required by Jackson
    }

    public Zone(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
