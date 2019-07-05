package io.metadew.iesi.metadata.definition;

public class ScenarioParameter {

    private String name;
    private String value;

    // Constructors
    public ScenarioParameter() {

    }

    public ScenarioParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}