package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;

import java.util.*;

public abstract class ScriptExecutionRequest extends Metadata<ScriptExecutionRequestKey> {

    private ExecutionRequestKey executionRequestKey;
    private boolean exit;
    private String impersonation;
    private String environment;
    private Map<String, String> impersonations;
    private Map<String, String> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;

    public ScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String environment, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey);
        this.environment = environment;
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
        this.parameters = new HashMap<>();
        this.impersonations = new HashMap<>();
        this.exit = true;
        this.executionRequestKey = executionRequestKey;
    }

    public ScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String environment,
                                  boolean exit, String impersonation, Map<String, String> impersonations,
                                  Map<String, String> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey);
        this.executionRequestKey = executionRequestKey;
        this.environment = environment;
        this.exit = exit;
        this.impersonation = impersonation;
        this.impersonations = impersonations;
        this.parameters = parameters;
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
    }

    public boolean isExit() {
        return exit;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Optional<String> getImpersonation() {
        return Optional.ofNullable(impersonation);
    }

    public Optional<Map<String, String>> getImpersonations() {
        return Optional.ofNullable(impersonations);
    }

    public String getEnvironment() {
        return environment;
    }

    public ScriptExecutionRequestStatus getScriptExecutionRequestStatus() {
        return scriptExecutionRequestStatus;
    }

    public void updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
    }

    public ExecutionRequestKey getExecutionRequestKey() {
        return executionRequestKey;
    }
}