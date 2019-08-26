package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ExecutionRequest extends Metadata<ExecutionRequestKey> {

    private LocalDateTime requestTimestamp;
    private String name;
    private String description;

    private String scope;
    private String context;

    private String email;
    private ExecutionRequestStatus executionRequestStatus;

    private List<ScriptExecutionRequest> scriptExecutionRequests;

    public ExecutionRequest(ExecutionRequestKey executionRequestKey, LocalDateTime requestTimestamp, String name, String description, String email,
                            String scope, String context, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequest> scriptExecutionRequests) {
        super(executionRequestKey);
        this.requestTimestamp = requestTimestamp;
        this.name = name;
        this.description = description;
        this.email = email;
        this.scope = scope;
        this.context = context;
        this.executionRequestStatus = executionRequestStatus;
        this.scriptExecutionRequests = scriptExecutionRequests;
    }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getScope() {
        return scope;
    }

    public String getContext() {
        return context;
    }

    public List<ScriptExecutionRequest> getScriptExecutionRequests() {
        return scriptExecutionRequests;
    }

    public void setScriptExecutionRequests(List<ScriptExecutionRequest> scriptExecutionRequests) {
        this.scriptExecutionRequests = scriptExecutionRequests;
    }

    public ExecutionRequestStatus getExecutionRequestStatus() {
        return executionRequestStatus;
    }
    public void updateExecutionRequestStatus(ExecutionRequestStatus executionRequestStatus) {
        this.executionRequestStatus = executionRequestStatus;
    }
}
