package io.metadew.iesi.server.rest.resource.execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ExecutionRequestDto extends RepresentationModel<ExecutionRequestDto> {

    private String executionRequestId;
    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private ExecutionRequestStatus executionRequestStatus;
    private List<ScriptExecutionRequestDto> scriptExecutionRequests;

    public ExecutionRequestDto(String executionRequestId, LocalDateTime requestTimestamp, String name, String description, String scope, String context,
                               String email, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequestDto> scriptExecutionRequests) {
        super();
        this.executionRequestId = executionRequestId;
        this.requestTimestamp = requestTimestamp;
        this.name = name;
        this.description = description;
        this.scope = scope;
        this.context = context;
        this.email = email;
        this.executionRequestStatus = executionRequestStatus;
        this.scriptExecutionRequests = scriptExecutionRequests;
    }

    public ExecutionRequest convertToEntity() {
        return new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(executionRequestId), requestTimestamp, name,
                context, description, scope, context, executionRequestStatus, scriptExecutionRequests.stream()
                .map(ScriptExecutionRequestDto::convertToEntity)
                .collect(Collectors.toList()));
    }

    public ExecutionRequest convertToNewEntity() throws ExecutionRequestBuilderException {
        ExecutionRequest executionRequest = new ExecutionRequestBuilder()
                .name(name)
                .context(context)
                .description(description)
                .scope(scope)
                .build();
        List<ScriptExecutionRequest> scriptExecutionRequests = new ArrayList<>();
        for (ScriptExecutionRequestDto scriptExecutionRequest : this.scriptExecutionRequests) {
            try {
                scriptExecutionRequests.add(scriptExecutionRequest.convertToNewEntity(executionRequest.getMetadataKey().getId()));
            } catch (ScriptExecutionRequestBuilderException e) {
                throw new RuntimeException(e);
            }
        }
        executionRequest.setScriptExecutionRequests(scriptExecutionRequests);
        return executionRequest;
    }

    // public abstract ExecutionRequest convertToEntity(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException;

}
