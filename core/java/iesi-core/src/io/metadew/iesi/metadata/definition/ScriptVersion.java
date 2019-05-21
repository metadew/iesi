package io.metadew.iesi.metadata.definition;

public class ScriptVersion {
    private long scriptId;
    private long number = 0;
    private String description = "Default version";

    // Constructors
    public ScriptVersion() {

    }

    public ScriptVersion(long scriptId, long number, String description) {
        this.scriptId = scriptId;
        this.number = number;
        this.description = description;
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


    public long getScriptId() {
        return scriptId;
    }
}