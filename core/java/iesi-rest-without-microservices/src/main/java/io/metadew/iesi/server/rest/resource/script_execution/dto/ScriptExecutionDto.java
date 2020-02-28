package io.metadew.iesi.server.rest.resource.script_execution.dto;

import io.metadew.iesi.framework.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptExecutionDto extends Dto {

    private String scriptExecutionId;
    private String scriptExecutionRequestId;
    private String runId;
    private ScriptRunStatus scriptRunStatus;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

    public ScriptExecution convertToEntity() {
        return new ScriptExecution(new ScriptExecutionKey(scriptExecutionId), new ScriptExecutionRequestKey(scriptExecutionRequestId),
                runId, scriptRunStatus,  startTimestamp, endTimestamp);
    }
}
