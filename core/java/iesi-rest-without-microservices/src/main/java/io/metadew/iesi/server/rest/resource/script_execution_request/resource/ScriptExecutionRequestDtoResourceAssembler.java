package io.metadew.iesi.server.rest.resource.script_execution_request.resource;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
import io.metadew.iesi.server.rest.controller.ScriptExecutionController;
import io.metadew.iesi.server.rest.resource.script_execution_request.dto.ScriptExecutionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

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
            return new ScriptExecutionRequestDto(scriptExecutionRequest.getMetadataKey().getId(), scriptExecutionRequest.getExecutionRequestKey().getId(),
                    scriptExecutionRequest.getActionSelect(), scriptExecutionRequest.isExit(), scriptExecutionRequest.getImpersonation().orElse(null),
                    scriptExecutionRequest.getEnvironment(), scriptExecutionRequest.getImpersonations().orElse(null), scriptExecutionRequest.getParameters(), scriptExecutionRequest.getScriptExecutionRequestStatus(),
                    ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptName(), ((ScriptNameExecutionRequest) scriptExecutionRequest).getScriptVersion().orElse(-1L));
        } else {
            throw new RuntimeException("");
        }
    }
}