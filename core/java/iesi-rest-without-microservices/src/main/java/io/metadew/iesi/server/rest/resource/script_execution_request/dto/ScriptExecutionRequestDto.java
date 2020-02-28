package io.metadew.iesi.server.rest.resource.script_execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptExecutionRequestDto extends Dto {

    private String scriptExecutionRequestId;
    private String executionRequestId;
    private String environment;
    private boolean exit;
    private List<ScriptExecutionRequestImpersonationDto> impersonations;
    private List<ScriptExecutionRequestParameterDto> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;
    private String scriptName;
    private Long scriptVersion;

    public ScriptNameExecutionRequest convertToEntity() {
        return new ScriptNameExecutionRequest(
                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                new ExecutionRequestKey(executionRequestId),
                environment, exit, impersonations.stream().map(impersonation -> impersonation.convertToEntity(new ScriptExecutionRequestKey(scriptExecutionRequestId))).collect(Collectors.toList()), parameters.stream().map(parameter -> parameter.convertToEntity(new ScriptExecutionRequestKey(scriptExecutionRequestId))).collect(Collectors.toList()), scriptExecutionRequestStatus, scriptName,
                scriptVersion
        );
    }

}
