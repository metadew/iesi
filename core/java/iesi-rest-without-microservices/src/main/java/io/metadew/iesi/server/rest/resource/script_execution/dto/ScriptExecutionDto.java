package io.metadew.iesi.server.rest.resource.script_execution.dto;

import io.metadew.iesi.framework.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ScriptExecutionDto extends RepresentationModel<ScriptExecutionDto> {

    private String scriptExecutionId;
    private ScriptRunStatus scriptRunStatus;
    private String runId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private String scriptExecutionRequestId;

    public ScriptExecutionDto(String scriptExecutionId, String scriptExecutionRequestId, ScriptRunStatus scriptRunStatus, String runId, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        this.scriptExecutionId = scriptExecutionId;
        this.scriptExecutionRequestId = scriptExecutionRequestId;
        this.scriptRunStatus = scriptRunStatus;
        this.runId = runId;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public ScriptExecution convertToEntity() {
        return new ScriptExecution(new ScriptExecutionKey(scriptExecutionId), new ScriptExecutionRequestKey(scriptExecutionRequestId),
                runId, scriptRunStatus,  startTimestamp, endTimestamp);
    }
}
