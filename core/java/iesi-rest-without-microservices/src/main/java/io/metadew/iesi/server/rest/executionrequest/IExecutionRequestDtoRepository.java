package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IExecutionRequestDtoRepository {

    Page<ExecutionRequestDto> getAll(Authentication authentication, Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters);

    Optional<ExecutionRequestDto> getById(Authentication authentication, UUID uuid);

}
