package io.metadew.iesi.server.rest.resource.execution_request.resource;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.execution_request.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.resource.script_execution_request.resource.ScriptExecutionRequestDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ExecutionRequestDtoResourceAssembler extends ResourceAssemblerSupport<ExecutionRequest, ExecutionRequestDto> {

    private final ScriptExecutionRequestDtoResourceAssembler scriptExecutionRequestDtoResourceAssembler;

    @Autowired
    public ExecutionRequestDtoResourceAssembler(ScriptExecutionRequestDtoResourceAssembler scriptExecutionRequestDtoResourceAssembler) {
        super(ExecutionRequestController.class, ExecutionRequestDto.class);
        this.scriptExecutionRequestDtoResourceAssembler = scriptExecutionRequestDtoResourceAssembler;
    }

    @Override
    public ExecutionRequestDto toResource(ExecutionRequest executionRequest) {
        ExecutionRequestDto executionRequestDto = convertToDto(executionRequest);
        Link selfLink = linkTo(methodOn(ExecutionRequestController.class).getById(executionRequestDto.getExecutionRequestId()))
                .withSelfRel();
        executionRequestDto.add(selfLink);
        return executionRequestDto;
    }

    private ExecutionRequestDto convertToDto(ExecutionRequest executionRequest) {
        if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            return new ExecutionRequestDto(executionRequest.getMetadataKey().getId(), executionRequest.getRequestTimestamp(),
                    executionRequest.getName(), executionRequest.getDescription(), executionRequest.getScope(),
                    executionRequest.getContext(), executionRequest.getEmail(), executionRequest.getExecutionRequestStatus(),
                    executionRequest.getScriptExecutionRequests().stream()
                            .map(scriptExecutionRequestDtoResourceAssembler::toResource)
                            .collect(Collectors.toList()));
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot convert ExecutionRequest of type {0} to DTO", executionRequest.getClass().getSimpleName()));
        }
    }
}