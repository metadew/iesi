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


    // runId and processId = PRIMARY KEYS
    private String runId; // RES_SCRIPT - TRC_DES_SCRIPT - ...
    private Long processId; // RES_SCRIPT - TRC_DES_SCRIPT - ...

    private Long parentProcessId; // RES_SCRIPT
    private String scriptId; // RES_SCRIPT
    private String scriptName; // RES_SCRIPT
    private Long scriptVersion; // RES_SCRIPT
    private String environment; // RES_SCRIPT
    private ScriptRunStatus status; // RES_SCRIPT
    private LocalDateTime startTimestamp; // RES_SCRIPT
    private LocalDateTime endTimestamp; // RES_SCRIPT

    // TRC_DES_SCRIPT_PAR - Primary Key: RunID PrcId ScriptParName
    // InputParameterDto : name, rawValue, ?resolvedValue?

    private Map<String, InputParametersDto> inputParameters = new HashMap<>();

    // TRC_DES_SCRIPT_LBL - Primary Key: RunId PrcId ScriptLabelId
    // Label: name, value
    private Map<String, ScriptLabelDto> designLabels = new HashMap<>();

    // EXE_REQ_LBL OR TRC_SCRIPT_LBL
    private Map<String, ExecutionRequestLabelDto> executionLabels = new HashMap<>();

    // action: runId, processId, type, name, description, condition,
    // stopOnError, expectedError, status, startTimestamp, endTimestamp
    private Map<ActionExecutionKey, ActionExecutionDtoBuildHelper> actions = new HashMap<>();

    // output: name, value
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
