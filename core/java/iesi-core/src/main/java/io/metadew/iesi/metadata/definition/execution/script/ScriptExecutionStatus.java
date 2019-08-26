package io.metadew.iesi.metadata.definition.execution.script;

public enum ScriptExecutionStatus {
    RUNNING("RUNNING"),
    COMPLETED("COMPLETED"),
    UNKNOWN("UNKNOWN");

    private String value;

    ScriptExecutionStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}