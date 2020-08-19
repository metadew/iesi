package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AuthenticatedExecutionRequest extends ExecutionRequest {

    private String space;
    private String user;
    private String password;

    @Builder
    public AuthenticatedExecutionRequest(ExecutionRequestKey executionRequestKey, LocalDateTime requestTimestamp, String name, String description,
                                         String email, String scope, String context, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequest> scriptExecutionRequests,
                                         Set<ExecutionRequestLabel> executionRequestLabels, String space, String user, String password) {
        super(executionRequestKey, requestTimestamp, name, description, email, scope, context, executionRequestStatus, scriptExecutionRequests, executionRequestLabels);
        this.space = space;
        this.user = user;
        this.password = password;
    }


    public String getSpace() {
        return space;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }


}
