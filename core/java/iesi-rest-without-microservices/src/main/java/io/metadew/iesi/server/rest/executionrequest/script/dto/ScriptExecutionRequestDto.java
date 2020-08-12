package io.metadew.iesi.server.rest.executionrequest.script.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionRequestDto extends RepresentationModel<ScriptExecutionRequestDto> {

    private String scriptExecutionRequestId;
    private String executionRequestId;
    private String environment;
    private boolean exit;
    private List<ScriptExecutionRequestImpersonationDto> impersonations;
    private List<ScriptExecutionRequestParameterDto> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;
    private String scriptName;
    private Long scriptVersion;
    private String runId;

    public ScriptNameExecutionRequest convertToEntity() {
        return new ScriptNameExecutionRequest(
                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                new ExecutionRequestKey(executionRequestId),
                environment, exit, impersonations.stream().map(impersonation -> impersonation.convertToEntity(new ScriptExecutionRequestKey(scriptExecutionRequestId))).collect(Collectors.toList()), parameters.stream().map(parameter -> parameter.convertToEntity(new ScriptExecutionRequestKey(scriptExecutionRequestId))).collect(Collectors.toList()), scriptExecutionRequestStatus, scriptName,
                scriptVersion
        );
    }

    public ScriptExecutionRequest convertToNewEntity(String executionRequestId) throws ScriptExecutionRequestBuilderException {
        String uuid = UUID.randomUUID().toString();
        return new ScriptExecutionRequestBuilder()
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(uuid))
                .mode("script")
                .executionRequestKey(new ExecutionRequestKey(executionRequestId))
                .environment(environment)
                .exit(exit)
                .impersonations(impersonations.stream()
                        .map(scriptExecutionRequestImpersonationDto -> scriptExecutionRequestImpersonationDto.convertToEntity(new ScriptExecutionRequestKey(uuid)))
                        .collect(Collectors.toList()))
                .parameters(parameters.stream()
                        .map(scriptExecutionRequestParameterDto -> scriptExecutionRequestParameterDto.convertToEntity(new ScriptExecutionRequestKey(uuid)))
                        .collect(Collectors.toList()))
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .build();
    }
}
