package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExecutionRequestService implements IExecutionRequestService {

    private ExecutionRequestConfiguration executionRequestConfiguration;

    private final ExecutionRequestDtoRepository executionRequestDtoRepository;

    private ExecutionRequestService(ExecutionRequestConfiguration executionRequestConfiguration, ExecutionRequestDtoRepository executionRequestDtoRepository) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executionRequestDtoRepository = executionRequestDtoRepository;
    }

    public List<ExecutionRequest> getAll(int limit, int pageNumber, List<String> column, List<String> sort, String filterColumn, String searchParam, String request_from, String request_to) {
        return executionRequestDtoRepository.getAll(limit, pageNumber, column, sort, filterColumn, searchParam, request_from, request_to);
    }

    public int getTotalPages(int limit, String filterColumn, String searchParam, String request_from, String request_to){
        return executionRequestDtoRepository.getTotalPages(limit, filterColumn, searchParam, request_from, request_to);
    }

    public Optional<ExecutionRequest> getById(String id) {
        return executionRequestConfiguration.get(new ExecutionRequestKey(id));
    }

    public ExecutionRequest createExecutionRequest(ExecutionRequestDto executionRequestDto) throws ExecutionRequestBuilderException {
        ExecutionRequest executionRequest = executionRequestDto.convertToNewEntity();
        executionRequestConfiguration.insert(executionRequest);
        return executionRequest;
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
