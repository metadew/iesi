package io.metadew.iesi.server.rest.script.dto.expansions;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionDto {

    private String id;
    private String runId;
    private ScriptRunStatus runStatus;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

}
