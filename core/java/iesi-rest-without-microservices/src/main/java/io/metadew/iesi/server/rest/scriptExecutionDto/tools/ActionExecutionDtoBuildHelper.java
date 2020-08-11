package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
public class ActionExecutionDtoBuildHelper {

    private String runId;
    private Long processId;
    private String type;
    private String name;
    private String description;
    private String condition;
    private boolean errorStop;
    private boolean errorExpected;
    private ScriptRunStatus status;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private Map<String, ActionInputParametersDto> inputParameters;
    private Map<String, OutputDto> output;

    public ActionExecutionDto toActionExecutionDto() {
        return ActionExecutionDto.builder()
                .runId(runId)
                .processId(processId)
                .type(type)
                .name(name)
                .description(description)
                .condition(condition)
                .errorStop(errorStop)
                .errorExpected(errorExpected)
                .status(status)
                .startTimestamp(startTimestamp)
                .endTimestamp(endTimestamp)
                .inputParameters(new ArrayList<>(inputParameters.values()))
                .output(new ArrayList<>(output.values()))
                .build();
    }

}
