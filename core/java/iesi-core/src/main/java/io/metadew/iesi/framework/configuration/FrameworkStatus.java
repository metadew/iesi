package io.metadew.iesi.framework.configuration;

public enum FrameworkStatus {
    NEW("NEW"),
    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    STOPPED("STOPPED"),
    ABORRTED("ABORTED"),
    SKIPPED("SKIPPED"),
    CANCELLED("CANCELLED"),
    KILLED("KILLED"),
    UNKOWN("UNKNOWN");

    private String value;

    FrameworkStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}