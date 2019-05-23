package io.metadew.iesi.server.execution.configuration;

public enum ExecutionServerServices {
    REQUESTOR("requestor"),
    SCHEDULER("scheduler");

    private String value;

    ExecutionServerServices(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}