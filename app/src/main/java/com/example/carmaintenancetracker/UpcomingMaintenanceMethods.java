package com.example.carmaintenancetracker;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;

public class UpcomingMaintenanceMethods {
    private static UpcomingMaintenanceMethods instance;

    //ADD VARIABLES HERE
    private String exampleVariable;

    //private constructor to prevent instantiation
    private UpcomingMaintenanceMethods() {}

    //access to the singleton
    public static synchronized UpcomingMaintenanceMethods getInstance() {
        if (instance == null) {
            instance = new UpcomingMaintenanceMethods();
        }
        return instance;
    }

    //ACCESS VARIABLES HERE
    public String getExampleVariable() {
        return exampleVariable;
    }

    //SET VARIABLES HERE
    public void setExampleVariable(String exampleVariable) {
        this.exampleVariable = exampleVariable;
    }

    //PRIVATE METHODS
    private Vector<String> extractString(String config) {
        Vector<String> result = new Vector<>();
        String[] parts = config.split(":"); // Split the string at the colon

        // Check if we got exactly two parts (miles and type)
        if (parts.length == 2) {
            result.add(parts[0].trim()); // Add miles, trim whitespace
            result.add(parts[1].trim()); // Add type, trim whitespace
        } else {
            // If not valid, return empty vector
            result.add(""); // Miles
            result.add(""); // Type
        }

        return result;
    }
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder capitalized = new StringBuilder();
        String[] words = input.split(" ");
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0))); // Capitalize first letter
                capitalized.append(word.substring(1).toLowerCase()); // Lowercase the rest of the word
                capitalized.append(" "); // Add space after the word
            }
        }
        return capitalized.toString().trim(); // Remove trailing space
    }


    //PUBLIC METHODS
    public String concatenateConfigStr(String oilConfig, String tireConfig, String brakeConfig, String interiorConfig, String engineConfig) {
        //assign maintenanceList
        Vector<Vector<String>> maintenanceList = new Vector<>();

        //extract each string
        if (!Objects.equals(oilConfig, "")) {
            Vector<String> oilVec = extractString(oilConfig);
            if (!Objects.equals(oilVec.get(0), "") && !Objects.equals(oilVec.get(1), "")) {
                maintenanceList.add(oilVec);
            }
        }
        if (!Objects.equals(tireConfig, "")) {
            Vector<String> tireVec = extractString(tireConfig);
            if (!Objects.equals(tireVec.get(0), "") && !Objects.equals(tireVec.get(1), "")) {
                maintenanceList.add(tireVec);
            }
        }
        if (!Objects.equals(brakeConfig, "")) {
            Vector<String> brakeVec = extractString(brakeConfig);
            if (!Objects.equals(brakeVec.get(0), "")) {
                maintenanceList.add(brakeVec);
            }
        }
        if (!Objects.equals(interiorConfig, "")) {
            Vector<String> interiorVec = extractString(interiorConfig);
            if (!Objects.equals(interiorVec.get(0), "")) {
                maintenanceList.add(interiorVec);
            }
        }
        if (!Objects.equals(engineConfig, "")) {
            Vector<String> engineVec = extractString(engineConfig);
            if (!Objects.equals(engineVec.get(0), "")) {
                maintenanceList.add(engineVec);
            }
        }

        //combine values
        Map<String, Vector<String>> maintenanceMap = new TreeMap<>();

        // Iterate through the list of vectors
        for (Vector<String> maintenance : maintenanceList) {
            String miles = maintenance.get(0); // Get the mileage
            String task = maintenance.get(1);  // Get the maintenance task

            // If the mileage is already in the map, add the task to its list
            if (maintenanceMap.containsKey(miles)) {
                Objects.requireNonNull(maintenanceMap.get(miles)).add(task);
            } else {
                // Otherwise, create a new list with the task
                Vector<String> tasks = new Vector<>();
                tasks.add(miles); // First element is the mileage
                tasks.add(task);  // Add the task
                maintenanceMap.put(miles, tasks);
            }
        }

        // Convert the map back to a vector of vectors
        Vector<Vector<String>> resultingMaintenanceList = new Vector<>();
        for (Map.Entry<String, Vector<String>> entry : maintenanceMap.entrySet()) {
            resultingMaintenanceList.add(entry.getValue());
        }

        //concatenate vector of vectors
        StringBuilder result = new StringBuilder();

        for (Vector<String> maintenance : resultingMaintenanceList) {
            // Get the mileage (first element) and create the heading
            String miles = maintenance.get(0);
            result.append("At ").append(miles).append(" miles:\n");

            // Append each maintenance task (starting from index 1)
            for (int i = 1; i < maintenance.size(); i++) {
                String task = maintenance.get(i);
                result.append(capitalize(task)).append("\n"); // Capitalize each maintenance task
            }

            // Add a new line after each mileage section
            result.append("\n\n");
        }

        return result.toString();
    }
}
