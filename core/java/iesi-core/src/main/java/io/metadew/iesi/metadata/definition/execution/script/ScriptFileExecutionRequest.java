package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;

import java.util.List;
import java.util.Map;

public class ScriptFileExecutionRequest extends ScriptExecutionRequest {

    private String fileName;

    public ScriptFileExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String fileName, String environment, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, scriptExecutionRequestStatus);
        this.fileName = fileName;
    }

    public ScriptFileExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey, ExecutionRequestKey executionRequestKey, String fileName, String environment,
                                      List<Long> actionSelect, boolean exit, String impersonation, Map<String, String> impersonations,
                                      Map<String, String> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus) {
        super(scriptExecutionRequestKey, executionRequestKey, environment, exit, impersonation, impersonations, parameters, scriptExecutionRequestStatus);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
