package io.metadew.iesi.server.rest.resource.execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.resource.Dto;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExecutionRequestDto extends Dto {

    private String executionRequestId;
    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private ExecutionRequestStatus executionRequestStatus;
    private List<ScriptExecutionRequestDto> scriptExecutionRequests;
    private List<ExecutionRequestLabelDto> executionRequestLabels;

    public ExecutionRequest convertToEntity() {
        return new NonAuthenticatedExecutionRequest(new ExecutionRequestKey(executionRequestId), requestTimestamp, name,
                context, description, scope, context, executionRequestStatus, scriptExecutionRequests.stream()
                .map(ScriptExecutionRequestDto::convertToEntity)
                .collect(Collectors.toList()),
                executionRequestLabels.stream()
                        .map(label -> label.convertToEntity(new ExecutionRequestKey(executionRequestId)))
                        .collect(Collectors.toList()));
    }

}
