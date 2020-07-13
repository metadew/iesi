package io.metadew.iesi.server.rest.scriptExecutionDto.ActionExecution;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.scriptExecutionDto.inputParameter.InputParametersDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActionExecutionDto {

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
    private List<InputParametersDto> inputParameters;

}
