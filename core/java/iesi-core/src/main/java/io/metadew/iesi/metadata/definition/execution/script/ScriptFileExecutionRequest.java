package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptFileExecutionRequest extends ScriptExecutionRequest {

    private String fileName;

    public ScriptFileExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey,
                                      String fileName, String environment, boolean exit,
                                      Set<ScriptExecutionRequestImpersonation> impersonations, Set<ScriptExecutionRequestParameter> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, exit, impersonations, parameters, scriptExecutionRequestStatus);
        this.fileName = fileName;
    }
}
