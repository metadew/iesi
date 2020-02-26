package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ExecutionRequest extends Metadata<ExecutionRequestKey> {

    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private ExecutionRequestStatus executionRequestStatus;
    private List<ScriptExecutionRequest> scriptExecutionRequests;
    private List<ExecutionRequestLabel> executionRequestLabels;

    public ExecutionRequest(ExecutionRequestKey executionRequestKey, LocalDateTime requestTimestamp, String name, String description, String email,
                            String scope, String context, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequest> scriptExecutionRequests,
                            List<ExecutionRequestLabel> executionRequestLabels) {
        super(executionRequestKey);
        this.requestTimestamp = requestTimestamp;
        this.name = name;
        this.description = description;
        this.email = email;
        this.scope = scope;
        this.context = context;
        this.executionRequestStatus = executionRequestStatus;
        this.scriptExecutionRequests = scriptExecutionRequests;
        this.executionRequestLabels = executionRequestLabels;
    }

}
