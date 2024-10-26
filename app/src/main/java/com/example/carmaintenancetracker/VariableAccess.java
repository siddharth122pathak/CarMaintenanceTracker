package com.example.carmaintenancetracker;

import java.util.Vector;

public class VariableAccess {
    private static VariableAccess instance;

    //ADD VARIABLES HERE
    private String exampleVariable;
    private String upcomingMaintenanceTitle;
    private String upcomingMaintenanceMiles;
    private String upcomingMaintenanceTime;
    private Vector<String> activeVehicle;
    private String oilConfig;
    private String oilConfigT; //T = time version vs miles version used above
    private String tireConfig;
    private String tireConfigT;
    private String brakeInspectionConfig;
    private String brakeInspectionConfigT;
    private String coolantConfig;
    private String coolantConfigT;
    private String cabinFilterConfig;
    private String cabinFilterConfigT;
    private String engineFilterConfig;
    private String engineFilterConfigT;
    private String sparkPlugsConfig;
    private String sparkPlugsConfigT;
    private String transmissionConfig;
    private String transmissionConfigT;

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
    public String getUpcomingMaintenanceTitle() { return upcomingMaintenanceTitle; }
    public String getUpcomingMaintenanceMiles() { return upcomingMaintenanceMiles; }
    public String getUpcomingMaintenanceTime() { return upcomingMaintenanceTime; }
    public Vector<String> getActiveVehicle() { return activeVehicle; }
    public String getOilConfig() { return oilConfig; }
    public String getOilConfigT() { return oilConfigT; }
    public String getTireConfig() { return tireConfig; }
    public String getTireConfigT() { return tireConfigT; }
    public String getBrakeInspectionConfig() { return brakeInspectionConfig; }
    public String getBrakeInspectionConfigT() { return brakeInspectionConfigT; }
    public String getCoolantConfig() { return coolantConfig; }
    public String getCoolantConfigT() { return coolantConfigT; }
    public String getCabinFilterConfig() { return cabinFilterConfig; }
    public String getCabinFilterConfigT() { return cabinFilterConfigT; }
    public String getEngineFilterConfig() { return engineFilterConfig; }
    public String getEngineFilterConfigT() { return engineFilterConfigT; }
    public String getSparkPlugsConfig() { return sparkPlugsConfig; }
    public String getSparkPlugsConfigT() { return sparkPlugsConfigT; }
    public String getTransmissionConfig() { return transmissionConfig; }
    public String getTransmissionConfigT() { return transmissionConfigT; }

    //SET VARIABLES HERE
    public void setExampleVariable(String exampleVariable) { this.exampleVariable = exampleVariable; }
    public void setUpcomingMaintenanceTitle(String upcomingMaintenanceTitle) { this.upcomingMaintenanceTitle = upcomingMaintenanceTitle; }
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
    public void setOilConfigT(String oilConfigT) { this.oilConfigT = oilConfigT; }
    public void setTireConfig(String tireConfig) { this.tireConfig = tireConfig; }
    public void setTireConfigT(String tireConfigT) { this.tireConfigT = tireConfigT; }
    public void setBrakeInspectionConfig(String brakeInspectionConfig) { this.brakeInspectionConfig = brakeInspectionConfig; }
    public void setBrakeInspectionConfigT(String brakeInspectionConfigT) { this.brakeInspectionConfigT = brakeInspectionConfigT; }
    public void setCoolantConfig(String coolantConfig) { this.coolantConfig = coolantConfig; }
    public void setCoolantConfigT(String coolantConfigT) { this.coolantConfigT = coolantConfigT; }
    public void setCabinFilterConfig(String cabinFilterConfig) { this.cabinFilterConfig = cabinFilterConfig; }
    public void setCabinFilterConfigT(String cabinFilterConfigT) { this.cabinFilterConfigT = cabinFilterConfigT; }
    public void setEngineFilterConfig(String engineFilterConfig) { this.engineFilterConfig = engineFilterConfig; }
    public void setEngineFilterConfigT(String engineFilterConfigT) { this.engineFilterConfigT = engineFilterConfigT; }
    public void setSparkPlugsConfig(String sparkPlugsConfig) { this.sparkPlugsConfig = sparkPlugsConfig; }
    public void setSparkPlugsConfigT(String sparkPlugsConfigT) { this.sparkPlugsConfigT = sparkPlugsConfigT; }
    public void setTransmissionConfig(String transmissionConfig) { this.transmissionConfig = transmissionConfig; }
    public void setTransmissionConfigT(String transmissionConfigT) { this.transmissionConfigT = transmissionConfigT; }
}
