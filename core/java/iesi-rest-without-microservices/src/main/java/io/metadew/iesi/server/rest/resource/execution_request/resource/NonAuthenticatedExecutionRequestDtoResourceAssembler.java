/*
package io.metadew.iesi.server.rest.resource.execution_request.resource;

import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.server.rest.controller.ExecutionRequestController;
import io.metadew.iesi.server.rest.resource.execution_request.dto.NonAuthenticatedExecutionRequestDto;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public  class NonAuthenticatedExecutionRequestDtoResourceAssembler extends ResourceAssemblerSupport<NonAuthenticatedExecutionRequest, NonAuthenticatedExecutionRequestDto> {

    public NonAuthenticatedExecutionRequestDtoResourceAssembler() {
        super(ExecutionRequestController.class, NonAuthenticatedExecutionRequestDto.class);
    }

    @Override
    public NonAuthenticatedExecutionRequestDto toResource(NonAuthenticatedExecutionRequest executionRequest) {
        NonAuthenticatedExecutionRequestDto nonAuthenticatedExecutionRequestDto = convertToDto(executionRequest);
        return null;
    }

    private NonAuthenticatedExecutionRequestDto convertToDto(NonAuthenticatedExecutionRequest nonAuthenticatedExecutionRequest) {
        return null;
    }
}*/
