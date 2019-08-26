package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;

import java.time.LocalDateTime;

public class ScriptExecution extends Metadata<ScriptExecutionKey> {

    private ScriptExecutionStatus scriptExecutionStatus;
    private String runId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private ScriptExecutionRequestKey scriptExecutionRequestKey;

    public ScriptExecution(ScriptExecutionKey scriptExecutionKey, ScriptExecutionRequestKey scriptExecutionRequestKey, String runId, ScriptExecutionStatus scriptExecutionStatus, LocalDateTime startTimestamp,
                           LocalDateTime endTimestamp) {
        super(scriptExecutionKey);
        this.scriptExecutionRequestKey = scriptExecutionRequestKey;
        this.runId = runId;
        this.scriptExecutionStatus = scriptExecutionStatus;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public ScriptExecutionStatus getScriptExecutionStatus() {
        return scriptExecutionStatus;
    }

    public void updateScriptExecutionStatus(ScriptExecutionStatus scriptExecutionStatus) {
        this.scriptExecutionStatus = scriptExecutionStatus;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public ScriptExecutionRequestKey getScriptExecutionRequestKey() {
        return scriptExecutionRequestKey;
    }

    public String getRunId() {
        return runId;
    }
}
