package io.metadew.iesi.server.rest.resource.script_execution.resource;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.server.rest.controller.ScriptExecutionController;
import io.metadew.iesi.server.rest.resource.script_execution.dto.ScriptExecutionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public  class ScriptExecutionDtoResourceAssembler extends ResourceAssemblerSupport<ScriptExecution, ScriptExecutionDto> {

    @Autowired
    public ScriptExecutionDtoResourceAssembler() {
        super(ScriptExecutionController.class, ScriptExecutionDto.class);
    }

    @Override
    public ScriptExecutionDto toResource(ScriptExecution scriptExecutionRequest) {
        return convertToDto(scriptExecutionRequest);
    }

    private ScriptExecutionDto convertToDto(ScriptExecution scriptExecutionRequest) {
        return new ScriptExecutionDto(scriptExecutionRequest.getMetadataKey().getId(),
                scriptExecutionRequest.getScriptExecutionRequestKey().getId(),
                scriptExecutionRequest.getRunId(),
                scriptExecutionRequest.getScriptRunStatus(),
                scriptExecutionRequest.getStartTimestamp(),
                scriptExecutionRequest.getEndTimestamp());
    }
}