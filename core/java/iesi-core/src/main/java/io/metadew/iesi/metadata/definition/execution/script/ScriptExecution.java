package io.metadew.iesi.metadata.definition.execution.script;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptExecution extends Metadata<ScriptExecutionKey> {

    private ScriptExecutionRequestKey scriptExecutionRequestKey;
    private String runId;
    private ScriptRunStatus scriptRunStatus;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

    @Builder
    public ScriptExecution(ScriptExecutionKey scriptExecutionKey, ScriptExecutionRequestKey scriptExecutionRequestKey, String runId, ScriptRunStatus scriptRunStatus, LocalDateTime startTimestamp,
                           LocalDateTime endTimestamp) {
        super(scriptExecutionKey);
        this.scriptExecutionRequestKey = scriptExecutionRequestKey;
        this.runId = runId;
        this.scriptRunStatus = scriptRunStatus;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public ScriptRunStatus getScriptRunStatus() {
        return scriptRunStatus;
    }

    public void updateScriptRunStatus(ScriptRunStatus scriptExecutionStatus) {
        this.scriptRunStatus = scriptExecutionStatus;
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
