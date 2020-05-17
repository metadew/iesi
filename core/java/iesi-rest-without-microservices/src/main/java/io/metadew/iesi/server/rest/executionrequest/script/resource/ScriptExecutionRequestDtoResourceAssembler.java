package io.metadew.iesi.server.rest.executionrequest.script.resource;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public  class ScriptExecutionRequestDtoResourceAssembler extends RepresentationModelAssemblerSupport<ScriptExecutionRequest, ScriptExecutionRequestDto> {


    @Autowired
    public ScriptExecutionRequestDtoResourceAssembler() {
        super(ExecutionRequestController.class, ScriptExecutionRequestDto.class);
    }

    @Override
    public ScriptExecutionRequestDto toModel(ScriptExecutionRequest scriptExecutionRequest) {
        return convertToDto(scriptExecutionRequest);
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