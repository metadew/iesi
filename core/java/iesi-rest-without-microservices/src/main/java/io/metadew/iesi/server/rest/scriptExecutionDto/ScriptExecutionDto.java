package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.ActionExecutionDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.ExecutionInputParameterDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.OutputDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
public class ScriptExecutionDto extends RepresentationModel<ScriptExecutionDto> {

    private String runId;
    private Long processId;

    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private Long scriptVersion;
    private String securityGroupName;
    private String environment;
    private String userId;
    private String username;
    private ScriptRunStatus status;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private List<ExecutionInputParameterDto> inputParameters = new ArrayList<>();
    private List<ScriptLabelDto> designLabels = new ArrayList<>();
    private List<ExecutionRequestLabelDto> executionLabels = new ArrayList<>();
    private List<ActionExecutionDto> actions = new ArrayList<>();
    private List<OutputDto> output = new ArrayList<>();

}
