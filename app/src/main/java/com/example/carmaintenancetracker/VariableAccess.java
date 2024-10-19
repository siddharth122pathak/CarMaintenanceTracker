package com.example.carmaintenancetracker;

import java.util.Vector;

public class VariableAccess {
    private static VariableAccess instance;

    //ADD VARIABLES HERE
    private String exampleVariable;
    private String upcomingMaintenanceMiles;
    private String upcomingMaintenanceTime;
    private Vector<String> activeVehicle;
    private String oilConfig;

    //private constructor to prevent instantiation
    private VariableAccess() {}

    //access to the singleton
    public static synchronized VariableAccess getInstance() {
        if (instance == null) {
            instance = new VariableAccess();
        }
        return instance;
    }

    //ACCESS VARIABLES HERE
    public String getExampleVariable() {
        return exampleVariable;
    }
    public String getUpcomingMaintenanceMiles() { return upcomingMaintenanceMiles; }
    public String getUpcomingMaintenanceTime() { return upcomingMaintenanceTime; }
    public Vector<String> getActiveVehicle() { return activeVehicle; }
    public String getOilConfig() { return oilConfig; }

    //SET VARIABLES HERE
    public void setExampleVariable(String exampleVariable) {
        this.exampleVariable = exampleVariable;
    }
    public void setUpcomingMaintenanceMiles(String upcomingMaintenanceMiles) { this.upcomingMaintenanceMiles = upcomingMaintenanceMiles; }
    public void setUpcomingMaintenanceTime(String upcomingMaintenanceTime) { this.upcomingMaintenanceTime = upcomingMaintenanceTime; }
    public void setActiveVehicle(String year, String make, String model) {
        Vector<String> vector = new Vector<>();
        vector.add(year);
        vector.add(make);
        vector.add(model);
        this.activeVehicle = vector;
    }
    public void setOilConfig(String oilConfig) { this.oilConfig = oilConfig; }
}
