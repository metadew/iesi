package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.executionrequest.executor.ExecutionRequestExecutorService;
import io.metadew.iesi.server.rest.user.UserDto;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
public class ExecutionRequestService implements IExecutionRequestService {

    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final ExecutionRequestExecutorService executionRequestExecutorService;
    private final ExecutionRequestDtoRepository executionRequestDtoRepository;
    private final UserDtoRepository userDtoRepository;

    private ExecutionRequestService(ExecutionRequestConfiguration executionRequestConfiguration, ExecutionRequestExecutorService executionRequestExecutorService, ExecutionRequestDtoRepository executionRequestDtoRepository, UserDtoRepository userDtoRepository) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executionRequestExecutorService = executionRequestExecutorService;
        this.executionRequestDtoRepository = executionRequestDtoRepository;
        this.userDtoRepository = userDtoRepository;
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
        executionRequestConfiguration.insert(executionRequest);
        executionRequestExecutorService.execute(executionRequest);
        return executionRequest;
    }

    public ExecutionRequest createExecutionRequest(ExecutionRequestPostDto executionRequestPostDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userDtoRepository.get(username)
                .orElseThrow(() -> new RuntimeException("Cannot find user :" + username));
        String newExecutionRequestId = UUID.randomUUID().toString();
        AuthenticatedExecutionRequest authenticatedExecutionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(newExecutionRequestId))
                .name(executionRequestPostDto.getName())
                .username(userDto.getUsername())
                .userID(userDto.getId().toString())
                .context(executionRequestPostDto.getContext())
                .description(executionRequestPostDto.getDescription())
                .scope(executionRequestPostDto.getScope())
                .executionRequestLabels(executionRequestPostDto.getExecutionRequestLabels().stream()
                        .map(executionRequestLabelDto -> executionRequestLabelDto.convertToEntity(new ExecutionRequestKey(newExecutionRequestId)))
                        .collect(Collectors.toSet()))
                .email(executionRequestPostDto.getEmail())
                .scriptExecutionRequests(executionRequestPostDto.getScriptExecutionRequests().stream()
                        .map(scriptExecutionRequestPostDto -> scriptExecutionRequestPostDto.convertToEntity(newExecutionRequestId))
                        .collect(Collectors.toList()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .requestTimestamp(LocalDateTime.now())
                .build();

        return createExecutionRequest(authenticatedExecutionRequest);
    }

    public void deleteAll() {
    }

    public void deleteById(String id) {
        executionRequestConfiguration.delete(new ExecutionRequestKey(id));
    }

}
