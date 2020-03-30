package io.metadew.iesi.server.rest.resource.script_execution_request.resource;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public  class ScriptExecutionRequestDtoResourceAssembler extends RepresentationModelAssemblerSupport<ScriptExecutionRequest, ScriptExecutionRequestDto> {

    @Autowired
    public ScriptExecutionRequestDtoResourceAssembler() {
        super(ExecutionRequestController.class, ScriptExecutionRequestDto.class);
    }

    @Override
    public ScriptExecutionRequestDto toModel(ScriptExecutionRequest scriptExecutionRequest) {
        ScriptExecutionRequestDto scriptExecutionRequestDto = convertToDto(scriptExecutionRequest);
        return scriptExecutionRequestDto;
    }

    private ScriptExecutionRequestDto convertToDto(ScriptExecutionRequest scriptExecutionRequest) {
        if (scriptExecutionRequest instanceof ScriptNameExecutionRequest) {
            return new ScriptExecutionRequestDto(scriptExecutionRequest.getMetadataKey().getId(), scriptExecutionRequest.getExecutionRequestKey().getId(),
                    scriptExecutionRequest.getActionSelect(), scriptExecutionRequest.isExit(), scriptExecutionRequest.getImpersonation().orElse(null),
                    scriptExecutionRequest.getEnvironment(), scriptExecutionRequest.getImpersonations().orElse(null), scriptExecutionRequest.getParameters(), scriptExecutionRequest.getScriptExecutionRequestStatus(),
                    ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName(), ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(-1L));
        } else {
            throw new RuntimeException("");
        }
    }
}