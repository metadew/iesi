package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestPostDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Builder
@Relation(value = "execution_request", collectionRelation = "execution_requests")
public class ExecutionRequestPostDto extends RepresentationModel<ExecutionRequestPostDto> {

    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private Set<ScriptExecutionRequestPostDto> scriptExecutionRequests = new HashSet<>();
    private Set<ExecutionRequestLabelDto> executionRequestLabels = new HashSet<>();


   /* public ExecutionRequest convertToEntity() {
        String newExecutionRequestId = UUID.randomUUID().toString();
        return AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(newExecutionRequestId))
                .name(name)
                .context(context)
                .description(description)
                .scope(scope)
                .executionRequestLabels(executionRequestLabels.stream()
                        .map(executionRequestLabelDto -> executionRequestLabelDto.convertToEntity(new ExecutionRequestKey(newExecutionRequestId)))
                        .collect(Collectors.toSet()))
                .email(email)
                .scriptExecutionRequests(scriptExecutionRequests.stream()
                        .map(scriptExecutionRequestPostDto -> scriptExecutionRequestPostDto.convertToEntity(newExecutionRequestId))
                        .collect(Collectors.toList()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .requestTimestamp(LocalDateTime.now())
                .build();


    }*/

}
