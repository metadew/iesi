package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Builder
@Relation(value = "execution_request", collectionRelation = "execution_requests")
public class ExecutionRequestDto extends RepresentationModel<ExecutionRequestDto> {

    private String executionRequestId;
    // private String securityGroupName;
    // private String securityGroupUuid;
    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private String userId;
    private String username;
    private boolean debugMode;
    private ExecutionRequestStatus executionRequestStatus;
    private Set<ScriptExecutionRequestDto> scriptExecutionRequests = new HashSet<>();
    private Set<ExecutionRequestLabelDto> executionRequestLabels = new HashSet<>();

}
