package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ExecutionRequestService implements IExecutionRequestService {

    private ExecutionRequestConfiguration executionRequestConfiguration;

    private ExecutionRequestService(ExecutionRequestConfiguration executionRequestConfiguration) {
        this.executionRequestConfiguration = executionRequestConfiguration;
    }

    public List<ExecutionRequest> getAll() {
        return executionRequestConfiguration.getAll();
    }

    public Optional<ExecutionRequest> getById(String id) {
        return executionRequestConfiguration.get(new ExecutionRequestKey(id));
    }

    public void createExecutionRequest(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException {
        ExecutionRequest executionRequest = executionRequestDto.convertToNewEntity();
        executionRequestConfiguration.insert(executionRequest);
    }

    public void updateExecutionRequest(ExecutionRequestDto executionRequestDto) {
        executionRequestConfiguration.update(executionRequestDto.convertToEntity());
    }

    public void updateExecutionRequests(List<ExecutionRequestDto> executionRequestDtos) {
        executionRequestDtos.forEach(this::updateExecutionRequest);
    }

    public void deleteAll() {
    }

    public void deleteById(String id) {
        executionRequestConfiguration.delete(new ExecutionRequestKey(id));
    }

}
