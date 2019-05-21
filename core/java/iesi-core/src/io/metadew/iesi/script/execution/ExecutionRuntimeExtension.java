package io.metadew.iesi.script.execution;

public class ExecutionRuntimeExtension {

    private String executionRuntimeExtensionName = "unknown";

    public ExecutionRuntimeExtension() {

    }

    public ExecutionRuntimeExtension(String executionRuntimeExtensionName) {
        this.setExecutionRuntimeExtensionName(executionRuntimeExtensionName);
    }

    // Getters and setters
    public String getExecutionRuntimeExtensionName() {
        return executionRuntimeExtensionName;
    }

    public void setExecutionRuntimeExtensionName(String executionRuntimeExtensionName) {
        this.executionRuntimeExtensionName = executionRuntimeExtensionName;
    }

}