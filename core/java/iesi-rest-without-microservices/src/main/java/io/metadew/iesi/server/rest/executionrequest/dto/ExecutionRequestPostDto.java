package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestPostDto;
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
public class ExecutionRequestPostDto extends RepresentationModel<ExecutionRequestPostDto> {

    private LocalDateTime requestTimestamp;
    private String name;
    private String description;
    private String scope;
    private String context;
    private String email;
    private Set<ScriptExecutionRequestPostDto> scriptExecutionRequests = new HashSet<>();
    private Set<ExecutionRequestLabelDto> executionRequestLabels = new HashSet<>();

}
