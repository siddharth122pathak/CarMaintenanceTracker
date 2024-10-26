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

    public String concatenateConfigStr(String oilConfig, String tireConfig, String brakeInspectionConfig, String cabinFilterConfig, String coolantConfig, String engineFilterConfig, String sparkPlugsConfig, String transmissionConfig, boolean time) {
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
        if (!Objects.equals(brakeInspectionConfig, "")) {
            Vector<String> brakeInspectionVec = extractString(brakeInspectionConfig);
            if (!Objects.equals(brakeInspectionVec.get(0), "")) {
                maintenanceList.add(brakeInspectionVec);
            }
        }
        if (!Objects.equals(cabinFilterConfig, "")) {
            Vector<String> cabinFilterVec = extractString(cabinFilterConfig);
            if (!Objects.equals(cabinFilterVec.get(0), "")) {
                maintenanceList.add(cabinFilterVec);
            }
        }
        if (!Objects.equals(coolantConfig, "")) {
            Vector<String> coolantVec = extractString(coolantConfig);
            if (!Objects.equals(coolantVec.get(0), "")) {
                maintenanceList.add(coolantVec);
            }
        }
        if (!Objects.equals(engineFilterConfig, "")) {
            Vector<String> engineFilterVec = extractString(engineFilterConfig);
            if (!Objects.equals(engineFilterVec.get(0), "")) {
                maintenanceList.add(engineFilterVec);
            }
        }
        if (!Objects.equals(sparkPlugsConfig, "")) {
            Vector<String> sparkPlugsVec = extractString(sparkPlugsConfig);
            if (!Objects.equals(sparkPlugsVec.get(0), "")) {
                maintenanceList.add(sparkPlugsVec);
            }
        }
        if (!Objects.equals(transmissionConfig, "")) {
            Vector<String> transmissionVec = extractString(transmissionConfig);
            if (!Objects.equals(transmissionVec.get(0), "")) {
                maintenanceList.add(transmissionVec);
            }
        }

        // Combine values using a TreeMap with Integer as key for numerical sorting
        Map<Integer, Vector<String>> maintenanceMap = new TreeMap<>();

        // Iterate through the list of vectors
        for (Vector<String> maintenance : maintenanceList) {
            int miles = Integer.parseInt(maintenance.get(0)); // Parse mileage as an integer
            String task = maintenance.get(1);  // Get the maintenance task

            // If the mileage is already in the map, add the task to its list
            if (maintenanceMap.containsKey(miles)) {
                Objects.requireNonNull(maintenanceMap.get(miles)).add(task);
            } else {
                // Otherwise, create a new list with the task
                Vector<String> tasks = new Vector<>();
                tasks.add(task);  // Add the task
                maintenanceMap.put(miles, tasks);
            }
        }

        //concatenate map entries into a result string
        StringBuilder result = new StringBuilder();

        // Iterate through the sorted map (TreeMap guarantees sorted keys numerically)
        for (Map.Entry<Integer, Vector<String>> entry : maintenanceMap.entrySet()) {
            int miles = entry.getKey();
            Vector<String> tasks = entry.getValue();

            //Check if method is for miles or time and build string accordingly
            if (!time) {
                result.append("At ").append(miles).append(" miles:\n");
            } else {
                boolean months = false;
                boolean years = false;

                if (miles >= 60) {
                    months = true;
                    miles /= 30;

                    if (miles >= 24) {
                        months = false;
                        years = true;
                        miles /= 12;
                    }
                }

                if (years) {
                    result.append("In ").append(miles).append(" years:\n");
                } else if (months) {
                    result.append("In ").append(miles).append(" months:\n");
                } else {
                    result.append("In ").append(miles).append(" days:\n");
                }
            }

            // Append each maintenance task
            for (String task : tasks) {
                result.append(capitalize(task)).append("\n"); // Capitalize each maintenance task
            }

            // Add a new line after each mileage section
            result.append("\n\n");
        }

        return result.toString();
    }
}
