package io.metadew.iesi.server.rest.scriptExecutionDto.tools;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.ScriptExecutionDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionDtoBuildHelper {


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
    private Map<String, InputParametersDto> inputParameters = new HashMap<>();
    private Map<String, ScriptLabelDto> designLabels = new HashMap<>();
    private Map<String, ExecutionRequestLabelDto> executionLabels = new HashMap<>();
    private Map<ActionExecutionKey, ActionExecutionDtoBuildHelper> actions = new HashMap<>();
    private Map<String, OutputDto> output = new HashMap<>();

    public ScriptExecutionDto toScriptExecutionDto() {
        return ScriptExecutionDto.builder()
                .runId(runId)
                .processId(processId)
                .parentProcessId(parentProcessId)
                .scriptId(scriptId)
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .environment(environment)
                .status(status)
                .startTimestamp(startTimestamp)
                .endTimestamp(endTimestamp)
                .inputParameters(new ArrayList<>(inputParameters.values()))
                .designLabels(new ArrayList<>(designLabels.values()))
                .executionLabels(new ArrayList<>(executionLabels.values()))
                .actions(actions.values().stream()
                        .map(ActionExecutionDtoBuildHelper::toActionExecutionDto)
                        .collect(Collectors.toList()))
                .output(new ArrayList<>(output.values()))
                .build();
    }

}
