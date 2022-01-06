package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ScriptExecutionRequest extends Metadata<ScriptExecutionRequestKey> {

    private ExecutionRequestKey executionRequestKey;
    private String environment;
    private Set<ScriptExecutionRequestImpersonation> impersonations;
    private Set<ScriptExecutionRequestParameter> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;

    public ScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey,
                                  String environment, Set<ScriptExecutionRequestImpersonation> impersonations,
                                  Set<ScriptExecutionRequestParameter> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey);
        this.executionRequestKey = executionRequestKey;
        this.environment = environment;
        this.impersonations = impersonations;
        this.parameters = parameters;
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
    }

}