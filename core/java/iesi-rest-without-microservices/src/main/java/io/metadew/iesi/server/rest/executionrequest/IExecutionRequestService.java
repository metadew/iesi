package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;

import java.util.List;
import java.util.Optional;

public interface IExecutionRequestService {

    public List<ExecutionRequestDto> getAll();

    Optional<ExecutionRequestDto> getById(String id);

    ExecutionRequest createExecutionRequest(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException;

    void updateExecutionRequest(ExecutionRequestDto executionRequestDto);

    void updateExecutionRequests(List<ExecutionRequestDto> executionRequestDtos);

    void deleteAll();

    void deleteById(String id);

}
