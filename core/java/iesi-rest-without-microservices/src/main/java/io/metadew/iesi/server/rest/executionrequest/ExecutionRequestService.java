package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExecutionRequestService implements IExecutionRequestService {

    private final ExecutionRequestConfiguration executionRequestConfiguration;

    private final ExecutionRequestDtoRepository executionRequestDtoRepository;

    private ExecutionRequestService(ExecutionRequestConfiguration executionRequestConfiguration, ExecutionRequestDtoRepository executionRequestDtoRepository) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executionRequestDtoRepository = executionRequestDtoRepository;
    }

    public Page<ExecutionRequestDto> getAll(Authentication authentication, Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        return executionRequestDtoRepository.getAll(authentication, pageable, executionRequestFilters);
    }

    public Optional<ExecutionRequestDto> getById(Authentication authentication, String id) {
        return executionRequestDtoRepository.getById(authentication, UUID.fromString(id));
    }

    public ExecutionRequest createExecutionRequest(ExecutionRequest executionRequest) {
        executionRequestConfiguration.insert(executionRequest);
        return executionRequest;
    }

    public void updateExecutionRequest(ExecutionRequestDto executionRequestDto) {
       // executionRequestConfiguration.update(executionRequestDto.convertToEntity());
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
