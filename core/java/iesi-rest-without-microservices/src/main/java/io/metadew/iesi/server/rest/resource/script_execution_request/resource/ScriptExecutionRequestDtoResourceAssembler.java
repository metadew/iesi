package io.metadew.iesi.server.rest.resource.script_execution_request.resource;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
import io.metadew.iesi.server.rest.controller.ScriptExecutionController;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestParameterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptExecutionRequestDtoResourceAssembler extends ResourceAssemblerSupport<ScriptExecutionRequest, ScriptExecutionRequestDto> {

    @Autowired
    public ScriptExecutionRequestDtoResourceAssembler() {
        super(ExecutionRequestController.class, ScriptExecutionRequestDto.class);
    }

    @Override
    public ScriptExecutionRequestDto toResource(ScriptExecutionRequest scriptExecutionRequest) {
        ScriptExecutionRequestDto scriptExecutionRequestDto = convertToDto(scriptExecutionRequest);
        Link selfLink = linkTo(methodOn(ScriptExecutionController.class).getAll(scriptExecutionRequest.getMetadataKey().getId()))
                .withRel("result");
        scriptExecutionRequestDto.add(selfLink);
        return scriptExecutionRequestDto;
    }

    private ScriptExecutionRequestDto convertToDto(ScriptExecutionRequest scriptExecutionRequest) {
        if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            return new ScriptExecutionRequestDto(scriptExecutionRequest.getMetadataKey().getId(),
                    scriptExecutionRequest.getExecutionRequestKey().getId(),
                    scriptExecutionRequest.getEnvironment(),
                    scriptExecutionRequest.isExit(),
                    scriptExecutionRequest.getImpersonations().stream().map(this::convertToDto).collect(Collectors.toList()),
                    scriptExecutionRequest.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()),
                    scriptExecutionRequest.getScriptExecutionRequestStatus(),
                    ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName(), ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(-1L));
        } else {
            throw new RuntimeException("");
        }
    }

    private ScriptExecutionRequestImpersonationDto convertToDto(ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation) {
        return new ScriptExecutionRequestImpersonationDto(scriptExecutionRequestImpersonation.getImpersonationKey().getName());
    }

    private ScriptExecutionRequestParameterDto convertToDto(ScriptExecutionRequestParameter scriptExecutionRequestParameter) {
        return new ScriptExecutionRequestParameterDto(scriptExecutionRequestParameter.getName(), scriptExecutionRequestParameter.getValue());
    }
}