package io.metadew.iesi.runtime.configuration;

public enum RequestStatus {
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

    RequestStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}