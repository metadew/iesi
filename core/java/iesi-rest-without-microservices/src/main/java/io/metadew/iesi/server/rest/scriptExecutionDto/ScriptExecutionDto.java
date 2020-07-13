package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.ActionExecution.ActionExecutionDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.inputParameter.InputParametersDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionDto extends RepresentationModel<ScriptExecutionDto> {

    private String runId;
    private Long processId;

    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private ScriptRunStatus status;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

    // InputParameterDto : name, rawValue, resolvedValue
    private List<InputParametersDto> inputParameters;

    // Label: name, value
    private List<ScriptLabelDto> designLabels;
    private List<ExecutionRequestLabelDto> executionLabels;

    // action: runId, processId, type, name, description, condition,
    // stopOnError, expectedError, status, startTimestamp, endTimestamp
    private List<ActionExecutionDto> actions;

    // output: name, value
    private List<ScriptLabelDto> output;

}
