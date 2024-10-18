package com.example.carmaintenancetracker;

public class VariableAccess {
    private static VariableAccess instance;

    //ADD VARIABLES HERE
    private String exampleVariable;
    private String upcomingMaintenanceMiles;
    private String upcomingMaintenanceTime;

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
    public String getUpcomingMaintenanceMiles() {
        return upcomingMaintenanceMiles;
    }
    public String getUpcomingMaintenanceTime() {
        return upcomingMaintenanceTime;
    }

    //SET VARIABLES HERE
    public void setExampleVariable(String exampleVariable) {
        this.exampleVariable = exampleVariable;
    }
    public void setUpcomingMaintenanceMiles(String upcomingMaintenanceMiles) { this.upcomingMaintenanceMiles = upcomingMaintenanceMiles; }
    public void setUpcomingMaintenanceTime(String upcomingMaintenanceTime) { this.upcomingMaintenanceTime = upcomingMaintenanceTime; }
}
