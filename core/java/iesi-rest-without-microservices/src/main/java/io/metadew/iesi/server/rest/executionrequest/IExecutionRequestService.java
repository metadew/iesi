package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;

import java.util.List;
import java.util.Optional;

public interface IExecutionRequestService {

    public List<ExecutionRequest> getAll();

    public Optional<ExecutionRequest> getById(String id);

    public ExecutionRequest createExecutionRequest(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException;

    public void updateExecutionRequest(ExecutionRequestDto executionRequestDto);

    public void updateExecutionRequests(List<ExecutionRequestDto> executionRequestDtos);

    public void deleteAll();

    public void deleteById(String id);

}
