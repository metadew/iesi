//package io.metadew.iesi.server.rest.resource.execution_request.resource;
//
//import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
//import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
//import io.metadew.iesi.server.rest.resource.execution_request.dto.AuthenticatedExecutionRequestDto;
//import io.metadew.iesi.server.rest.resource.script_execution_request.resource.ScriptExecutionRequestDtoResourceAssembler;
//import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
//import org.springframework.stereotype.Component;
//
//import java.util.stream.Collectors;
//
//@Component
//public  class AuthenticatedExecutionRequestDtoResourceAssembler extends ResourceAssemblerSupport<AuthenticatedExecutionRequest, AuthenticatedExecutionRequestDto> {
//
//    private final ScriptExecutionRequestDtoResourceAssembler scriptExecutionRequestDtoResourceAssembler;
//
//    public AuthenticatedExecutionRequestDtoResourceAssembler(ScriptExecutionRequestDtoResourceAssembler scriptExecutionRequestDtoResourceAssembler) {
//        super(ExecutionRequestController.class, AuthenticatedExecutionRequestDto.class);
//        this.scriptExecutionRequestDtoResourceAssembler = scriptExecutionRequestDtoResourceAssembler;
//    }
//
//    @Override
//    public AuthenticatedExecutionRequestDto toResource(AuthenticatedExecutionRequest executionRequest) {
//        AuthenticatedExecutionRequestDto authenticatedExecutionRequestDto = convertToDto(executionRequest);
//        return null;
//    }
//
//    private AuthenticatedExecutionRequestDto convertToDto(AuthenticatedExecutionRequest authenticatedExecutionRequest) {
//        return new AuthenticatedExecutionRequestDto(authenticatedExecutionRequest.getRequestTimestamp(),
//                authenticatedExecutionRequest.getName(), authenticatedExecutionRequest.getDescription(),
//                authenticatedExecutionRequest.getScope(), authenticatedExecutionRequest.getContext(),
//                authenticatedExecutionRequest.getEmail(), authenticatedExecutionRequest.getExecutionRequestStatus(),
//                authenticatedExecutionRequest.getScriptExecutionRequests().stream()
//                        .parallel()
//                        .map(scriptExecutionRequestDtoResourceAssembler::toResource)
//                        .collect(Collectors.toList()),
//                authenticatedExecutionRequest.getSpace(),
//                authenticatedExecutionRequest.getUser(), authenticatedExecutionRequest.getPassword());
//    }
//}