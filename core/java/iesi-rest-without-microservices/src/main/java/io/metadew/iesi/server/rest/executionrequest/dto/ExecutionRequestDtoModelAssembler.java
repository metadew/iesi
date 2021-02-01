package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDtoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExecutionRequestDtoModelAssembler extends RepresentationModelAssemblerSupport<ExecutionRequest, ExecutionRequestDto> {

    private final ScriptExecutionRequestDtoModelAssembler scriptExecutionRequestDtoModelAssembler;

    @Autowired
    public ExecutionRequestDtoModelAssembler(ScriptExecutionRequestDtoModelAssembler scriptExecutionRequestDtoModelAssembler) {
        super(ExecutionRequestController.class, ExecutionRequestDto.class);
        this.scriptExecutionRequestDtoModelAssembler = scriptExecutionRequestDtoModelAssembler;
    }

    @Override
    public ExecutionRequestDto toModel(ExecutionRequest executionRequest) {
        ExecutionRequestDto executionRequestDto = convertToDto(executionRequest);
        Link selfLink = linkTo(methodOn(ExecutionRequestController.class).getById(executionRequestDto.getExecutionRequestId()))
                .withSelfRel();
        executionRequestDto.add(selfLink);
        return executionRequestDto;
    }

    public ExecutionRequestDto toModel(ExecutionRequestDto executionRequestDto) {
        addLinksToExistingDto(executionRequestDto);
        return executionRequestDto;
    }

    public void addLinksToExistingDto(ExecutionRequestDto executionRequestDto) {
        Link selfLink = linkTo(methodOn(ExecutionRequestController.class).getById(executionRequestDto.getExecutionRequestId()))
                .withSelfRel();
        Link linkToAll = linkTo(methodOn(ExecutionRequestController.class).getAll(PageRequest.of(0, 20), null, null, null, null))
                .withRel("executionrequest");
        executionRequestDto.add(selfLink, linkToAll);
    }

    private ExecutionRequestDto convertToDto(ExecutionRequest executionRequest) {
        if (executionRequest instanceof NonAuthenticatedExecutionRequest) {
            return new ExecutionRequestDto(
                    executionRequest.getMetadataKey().getId(),
                    // executionRequest.getSecurityGroupKey().getUuid().toString(),
                    // executionRequest.getSecurityGroupName(),
                    executionRequest.getRequestTimestamp(),
                    executionRequest.getName(), executionRequest.getDescription(), executionRequest.getScope(),
                    executionRequest.getContext(), executionRequest.getEmail(), executionRequest.getExecutionRequestStatus(),
                    executionRequest.getScriptExecutionRequests().stream()
                            .map(scriptExecutionRequestDtoModelAssembler::toModel)
                            .collect(Collectors.toSet()),
                    executionRequest.getExecutionRequestLabels().stream()
                            .map(this::convertToDto)
                            .collect(Collectors.toSet()));
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot convert ExecutionRequest of type {0} to DTO", executionRequest.getClass().getSimpleName()));
        }
    }

    private ExecutionRequestLabelDto convertToDto(ExecutionRequestLabel executionRequestLabel) {
        return new ExecutionRequestLabelDto(executionRequestLabel.getName(), executionRequestLabel.getValue());
    }
}