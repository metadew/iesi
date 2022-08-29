package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AuthenticatedExecutionRequest extends ExecutionRequest {

    @Setter
    private String userID;

    @Setter
    private String username;

    @Builder
    public AuthenticatedExecutionRequest(ExecutionRequestKey executionRequestKey,
                                         // SecurityGroupKey securityGroupKey,
                                         // String securityGroupName,
                                         LocalDateTime requestTimestamp,
                                         String name,
                                         String description,
                                         String email,
                                         String scope,
                                         String context,
                                         boolean debugMode,
                                         ExecutionRequestStatus executionRequestStatus,
                                         List<ScriptExecutionRequest> scriptExecutionRequests,
                                         Set<ExecutionRequestLabel> executionRequestLabels,
                                         String userID,
                                         String username
    ) {
        super(executionRequestKey,
                // securityGroupKey,
                // securityGroupName,
                requestTimestamp,
                name,
                description,
                email,
                scope,
                context,
                debugMode,
                executionRequestStatus,
                scriptExecutionRequests,
                executionRequestLabels);
        this.userID = userID;
        this.username = username;

    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }
}
