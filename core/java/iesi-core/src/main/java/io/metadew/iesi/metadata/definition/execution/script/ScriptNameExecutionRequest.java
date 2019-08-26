package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScriptNameExecutionRequest extends ScriptExecutionRequest {

    private String scriptName;
    private Long scriptVersion;

    public ScriptNameExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String environment, String scriptName, Long scriptVersion, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, scriptExecutionRequestStatus);
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
    }

    public ScriptNameExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String scriptName, Long scriptVersion, String environment, List<Long> actionSelect, boolean exit, String impersonation, Map<String, String> impersonations, Map<String, String> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, actionSelect, exit, impersonation, impersonations, parameters, scriptExecutionRequestStatus);
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Optional<Long> getScriptVersion() {
        return Optional.ofNullable(scriptVersion);
    }
}
