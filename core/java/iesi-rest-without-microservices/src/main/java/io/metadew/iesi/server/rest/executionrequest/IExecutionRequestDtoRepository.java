package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IExecutionRequestDtoRepository {

    Page<ExecutionRequestDto> getAll(Pageable pageable, List<ExecutionRequestFilter> executionRequestFilters);

    Optional<ExecutionRequestDto> getById(UUID uuid);

}
