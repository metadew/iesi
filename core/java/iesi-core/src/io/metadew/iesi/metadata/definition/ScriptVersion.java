package io.metadew.iesi.metadata.definition;

public class ScriptVersion {

    private long number = 0;
    private String description = "Default version";

    // Constructors
    public ScriptVersion() {

    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }


}