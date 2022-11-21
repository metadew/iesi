package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.executor.ExecutionRequestExecutorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@ConditionalOnWebApplication
public class ExecutionRequestService implements IExecutionRequestService {

    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final ExecutionRequestExecutorService executionRequestExecutorService;
    private final ExecutionRequestDtoRepository executionRequestDtoRepository;

    private final MetadataPolicyConfiguration metadataPolicyConfiguration;

    public ExecutionRequestService(
            ExecutionRequestConfiguration executionRequestConfiguration,
            ExecutionRequestExecutorService executionRequestExecutorService,
            ExecutionRequestDtoRepository executionRequestDtoRepository,
            MetadataPolicyConfiguration metadataPolicyConfiguration
    ) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executionRequestExecutorService = executionRequestExecutorService;
        this.executionRequestDtoRepository = executionRequestDtoRepository;
        this.metadataPolicyConfiguration = metadataPolicyConfiguration;
    }


    @PostConstruct
    public void init() {
        // Called from here and not in post construct of ExecutionRequestExecutorService because otherwise the @Async (aspect) will not be picked up
        List<ExecutionRequest> oldExecutionRequests = executionRequestConfiguration.getAllNew();
        oldExecutionRequests.forEach(executionRequestExecutorService::execute);
    }

    public Page<ExecutionRequestDto> getAll(Authentication authentication, Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters) {
        return executionRequestDtoRepository.getAll(authentication, pageable, executionRequestFilters);
    }

    public Optional<ExecutionRequestDto> getById(Authentication authentication, String id) {
        return executionRequestDtoRepository.getById(authentication, UUID.fromString(id));
    }

    public ExecutionRequest createExecutionRequest(ExecutionRequest executionRequest) {
        metadataPolicyConfiguration.verifyExecutionRequestPolicies(executionRequest);
        executionRequestConfiguration.insert(executionRequest);
        executionRequestExecutorService.execute(executionRequest);
        return executionRequest;
    }

    public void deleteAll() {
    }

    public void deleteById(String id) {
        executionRequestConfiguration.delete(new ExecutionRequestKey(id));
    }

}
