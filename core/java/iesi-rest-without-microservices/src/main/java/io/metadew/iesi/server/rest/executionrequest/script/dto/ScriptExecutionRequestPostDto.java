package io.metadew.iesi.server.rest.executionrequest.script.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptExecutionRequestPostDto extends RepresentationModel<ScriptExecutionRequestPostDto> {

    private String environment;
    private boolean exit;
    private Set<ScriptExecutionRequestImpersonationDto> impersonations = new HashSet<>();
    private Set<ScriptExecutionRequestParameterDto> parameters = new HashSet<>();
    private String scriptName;
    private Long scriptVersion;

    public ScriptExecutionRequest convertToEntity(String executionRequestId) {
        String uuid = UUID.randomUUID().toString();
        return ScriptNameExecutionRequest.builder()
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(uuid))
                .executionRequestKey(new ExecutionRequestKey(executionRequestId))
                .environment(environment)
                .exit(exit)
                .impersonations(impersonations.stream()
                        .map(scriptExecutionRequestImpersonationDto -> scriptExecutionRequestImpersonationDto.convertToEntity(new ScriptExecutionRequestKey(uuid)))
                        .collect(Collectors.toSet()))
                .parameters(parameters.stream()
                        .map(scriptExecutionRequestParameterDto -> scriptExecutionRequestParameterDto.convertToEntity(new ScriptExecutionRequestKey(uuid)))
                        .collect(Collectors.toSet()))
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                .build();
    }
}
