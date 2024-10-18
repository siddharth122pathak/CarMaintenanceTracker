package com.example.carmaintenancetracker;

public class VariableAccess {
    private static VariableAccess instance;

    //ADD VARIABLES HERE
    private String exampleVariable;

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

    //SET VARIABLES HERE
    public void setExampleVariable(String exampleVariable) {
        this.exampleVariable = exampleVariable;
    }
}
