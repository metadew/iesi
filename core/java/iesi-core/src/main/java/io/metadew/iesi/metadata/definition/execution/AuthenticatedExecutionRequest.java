package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AuthenticatedExecutionRequest extends ExecutionRequest {

    private String userID;
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
