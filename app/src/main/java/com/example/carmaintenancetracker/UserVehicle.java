package com.example.carmaintenancetracker;

public class UserVehicle {
    private int car_id;
    private String make;
    private String model;
    private String year;
    private String nickname;

    //Getters and Setters

    public int getCarId() {
        return car_id;
    }

    public void setCarId(int car_id) {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}