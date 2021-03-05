package io.metadew.iesi.server.rest.scriptResultDto.dto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptResultDto extends RepresentationModel<ScriptResultDto> {

    // ScriptResultKey
    private String runID;
    private Long processId;

    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private ScriptRunStatus status; // Enum
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;


    public ScriptResultDto(ScriptResult scriptResult) {
        this.runID = scriptResult.getMetadataKey().getRunId();
        this.processId = scriptResult.getMetadataKey().getProcessId();
        this.parentProcessId = scriptResult.getParentProcessId();
        this.scriptId = scriptResult.getScriptId();
        this.scriptName = scriptResult.getScriptName();
        this.scriptVersion = scriptResult.getScriptVersion();
        this.environment = scriptResult.getEnvironment();
        this.status = scriptResult.getStatus();
        this.startTimestamp = scriptResult.getStartTimestamp();
        this.endTimestamp = scriptResult.getEndTimestamp();
    }
}
