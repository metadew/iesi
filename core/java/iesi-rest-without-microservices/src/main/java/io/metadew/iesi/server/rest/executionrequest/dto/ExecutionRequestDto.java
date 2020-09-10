package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Builder
@Relation(value = "execution_request", collectionRelation = "execution_requests")
public class ExecutionRequestDto extends RepresentationModel<ExecutionRequestDto> {

    private String executionRequestId;
    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private ExecutionRequestStatus executionRequestStatus;
    private List<ScriptExecutionRequestDto> scriptExecutionRequests = new ArrayList<>();
    private Set<ExecutionRequestLabelDto> executionRequestLabels = new HashSet<>();

    public ExecutionRequest convertToEntity() {
        return new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(executionRequestId), requestTimestamp, name,
                context, description, scope, context, executionRequestStatus, scriptExecutionRequests.stream()
                .map(ScriptExecutionRequestDto::convertToEntity)
                .collect(Collectors.toList()),
                executionRequestLabels.stream()
                        .map(label -> label.convertToEntity(new ExecutionRequestKey(executionRequestId)))
                        .collect(Collectors.toSet()));
    }

    public ExecutionRequest convertToNewEntity() throws ExecutionRequestBuilderException {
        String newExecutionRequestId = executionRequestId == null ? UUID.randomUUID().toString() : executionRequestId;
        ExecutionRequest executionRequest = new ExecutionRequestBuilder()
                .id(newExecutionRequestId)
                .name(name)
                .context(context)
                .description(description)
                .scope(scope)
                .executionRequestLabels(executionRequestLabels.stream()
                        .map(executionRequestLabelDto -> executionRequestLabelDto.convertToEntity(new ExecutionRequestKey(newExecutionRequestId)))
                        .collect(Collectors.toList()))
                .email(email)
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
