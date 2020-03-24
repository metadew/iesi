package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;

import java.time.LocalDateTime;
import java.util.List;

public class NonAuthenticatedExecutionRequest extends ExecutionRequest {

    public NonAuthenticatedExecutionRequest(ExecutionRequestKey executionRequestKey, LocalDateTime requestTimestamp, String name, String description, String email, String scope, String context, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequest> scriptExecutionRequests, List<ExecutionRequestLabel> executionRequestLabels) {
        super(executionRequestKey, requestTimestamp, name, description, email, scope, context, executionRequestStatus, scriptExecutionRequests, executionRequestLabels);
    }
}
