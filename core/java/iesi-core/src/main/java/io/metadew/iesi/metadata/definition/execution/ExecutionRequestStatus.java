package io.metadew.iesi.metadata.definition.execution;

public enum ExecutionRequestStatus {
    NEW("NEW"),
    SUBMITTED("SUBMITTED"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED"),
    STOPPED("STOPPED"),
    COMPLETED("COMPLETED"),
    KILLED("KILLED"),
    UNKNOWN("UNKNOWN");

    private String value;

    ExecutionRequestStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}