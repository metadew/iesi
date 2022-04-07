package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IExecutionRequestService {

    Page<ExecutionRequestDto> getAll(Authentication authentication, Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters);

    Optional<ExecutionRequestDto> getById(Authentication authentication, String id);

    ExecutionRequest createExecutionRequest(ExecutionRequest executionRequest);

    void deleteAll();

    void deleteById(String id);

}
