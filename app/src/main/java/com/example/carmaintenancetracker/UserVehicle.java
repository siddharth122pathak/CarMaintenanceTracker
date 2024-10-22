package com.example.carmaintenancetracker;

public class UserVehicle {
    private String car_id;
    private String make;
    private String model;
    private int year;
    private String nickname;
    private int mileage;
    private boolean notificationsOn;
    private long lastUpdatedTimestamp;
    private boolean active; //New field to indicate if the vehicle is active


    //Getters and Setters

    public String getCarId() {
        return car_id;
    }

    public void setCarId(String car_id) {
        this.car_id = car_id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean isNotificationsOn() {
        return notificationsOn;
    }

    public void setNotificationsOn(boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    // New getter and setter for the active field
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}