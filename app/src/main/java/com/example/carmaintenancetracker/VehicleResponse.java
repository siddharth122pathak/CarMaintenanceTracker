package com.example.carmaintenancetracker;

import java.util.List;

public class VehicleResponse {
    private String status;
    private List<UserVehicle> vehicles;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserVehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<UserVehicle> vehicles) {
        this.vehicles = vehicles;
    }
}