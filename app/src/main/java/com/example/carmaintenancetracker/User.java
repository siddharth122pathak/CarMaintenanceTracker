package com.example.carmaintenancetracker;

public class User {

    private int id;
    private String username;
    private String name;
    private String email;
    private String phone_number;
    private String password;
    private String creation_date;

    // Getters and setters for the fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phone_number; }
    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreationDate() { return creation_date; }
    public void setCreationDate(String creation_date) { this.creation_date = creation_date; }
}
