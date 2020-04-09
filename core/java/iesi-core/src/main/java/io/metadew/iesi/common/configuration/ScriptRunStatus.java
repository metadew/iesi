package io.metadew.iesi.common.configuration;

public enum ScriptRunStatus {
    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    STOPPED("STOPPED"),
    SKIPPED("SKIPPED");

    private String value;

    ScriptRunStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}