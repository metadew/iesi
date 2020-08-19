package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptNameExecutionRequest extends ScriptExecutionRequest {

    private String scriptName;
    private Long scriptVersion;

    @Builder
    public ScriptNameExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey,
                                      String environment, boolean exit, List<ScriptExecutionRequestImpersonation> impersonations, List<ScriptExecutionRequestParameter> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus, String scriptName, Long scriptVersion) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, exit, impersonations, parameters, scriptExecutionRequestStatus);
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
    }

    public Optional<Long> getScriptVersion() {
        return Optional.ofNullable(scriptVersion);
    }
}
