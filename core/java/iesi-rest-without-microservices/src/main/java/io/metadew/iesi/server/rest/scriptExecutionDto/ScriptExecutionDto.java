package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.tools.ActionExecutionDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.tools.InputParametersDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.tools.OutputDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
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
    private List<InputParametersDto> inputParameters = new ArrayList<>();
    private List<ScriptLabelDto> designLabels = new ArrayList<>();
    private List<ExecutionRequestLabelDto> executionLabels = new ArrayList<>();
    private List<ActionExecutionDto> actions = new ArrayList<>();
    private List<OutputDto> output = new ArrayList<>();

}
