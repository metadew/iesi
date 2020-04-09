package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ScriptExecutionRequest extends Metadata<ScriptExecutionRequestKey> {

    private ExecutionRequestKey executionRequestKey;
    private boolean exit;
    private String environment;
    private List<ScriptExecutionRequestImpersonation> impersonations;
    private List<ScriptExecutionRequestParameter> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;

    public ScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey,
                                  String environment, boolean exit, List<ScriptExecutionRequestImpersonation> impersonations,
                                  List<ScriptExecutionRequestParameter> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey);
        this.executionRequestKey = executionRequestKey;
        this.environment = environment;
        this.exit = exit;
        this.impersonations = impersonations;
        this.parameters = parameters;
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
    }

}