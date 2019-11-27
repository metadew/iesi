//package io.metadew.iesi.server.rest.resource.execution_request.dto;
//
//import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
//import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
//import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode(callSuper = false)
//public class AuthenticatedExecutionRequestDto extends ExecutionRequestDto {
//
//    private String space;
//    private String user;
//    private String password;
//
//    public AuthenticatedExecutionRequestDto(String id, LocalDateTime requestTimestamp, String name, String description, String scope,
//                                            String context, String email, ExecutionRequestStatus executionRequestStatus,
//                                            List<ScriptExecutionRequestDto> scriptExecutionRequests, String space, String user, String password) {
//        super(id, requestTimestamp, name, description, scope, context, email, executionRequestStatus, scriptExecutionRequests);
//        this.space = space;
//        this.user = user;
//        this.password = password;
//    }
//
//    @Override
//    public AuthenticatedExecutionRequest convertToEntity(ExecutionRequestDto executionRequestDto) {
//        return null;
//    }
//}
