//package io.metadew.iesi.server.rest.resource.execution_request.dto;
//
//import io.metadew.iesi.metadata.definition.execution.*;
//import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode(callSuper = false)
//public class NonAuthenticatedExecutionRequestDto extends ExecutionRequestDto {
//
//    public NonAuthenticatedExecutionRequestDto(String executionRequestId, LocalDateTime requestTimestamp, String name, String description, String scope,
//                                               String context, String email, ExecutionRequestStatus executionRequestStatus, List<ScriptExecutionRequestDto> scriptExecutionRequests) {
//        super(executionRequestId, requestTimestamp, name, description, scope, context, email, executionRequestStatus, scriptExecutionRequests);
//    }
//
//    @Override
//    public ExecutionRequest convertToEntity(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException {
//        return new ExecutionRequestBuilder()
//                .name(executionRequestDto.getName())
//                .context(executionRequestDto.getContext())
//                .description(executionRequestDto.getDescription())
//                .scope(executionRequestDto.getScope())
//                .scriptExecutionRequests(executionRequestDto.getScriptExecutionRequests().stream()
//                        .map(ScriptExecutionRequestDto::convertToEntity)
//                        .collect(Collectors.toList()))
//                .build();
//    }
//
//}
