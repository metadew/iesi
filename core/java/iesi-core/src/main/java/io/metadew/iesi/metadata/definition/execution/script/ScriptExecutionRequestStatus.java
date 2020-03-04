package io.metadew.iesi.metadata.definition.execution.script;

public enum ScriptExecutionRequestStatus {
    NEW("NEW"),
    SUBMITTED("SUBMITTED"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED"),
    COMPLETED("COMPLETED"),
    ABORTED("ABORTED"),
    UNKNOWN("UNKNOWN");

    private String value;

    ScriptExecutionRequestStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}