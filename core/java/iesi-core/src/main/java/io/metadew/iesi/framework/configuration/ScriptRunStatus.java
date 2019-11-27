package io.metadew.iesi.framework.configuration;

public enum ScriptRunStatus {
    NEW("NEW"),
    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    STOPPED("STOPPED"),
    ABORTED("ABORTED"),
    SKIPPED("SKIPPED"),
    CANCELLED("CANCELLED"),
    KILLED("KILLED"),
    UNKNOWN("UNKNOWN");

    private String value;

    ScriptRunStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}