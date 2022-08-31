package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ExecutionRequest extends Metadata<ExecutionRequestKey> {

    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private boolean debugMode;
    private ExecutionRequestStatus executionRequestStatus;
    private List<ScriptExecutionRequest> scriptExecutionRequests;
    private Set<ExecutionRequestLabel> executionRequestLabels;

    public ExecutionRequest(ExecutionRequestKey executionRequestKey,
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
                            Set<ExecutionRequestLabel> executionRequestLabels) {
        super(executionRequestKey
        //        ,securityGroupKey,
        //        securityGroupName
        );
        this.requestTimestamp = requestTimestamp;
        this.name = name;
        this.description = description;
        this.email = email;
        this.scope = scope;
        this.context = context;
        this.debugMode = debugMode;
        this.executionRequestStatus = executionRequestStatus;
        this.scriptExecutionRequests = scriptExecutionRequests;
        this.executionRequestLabels = executionRequestLabels;
    }

}
