package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IExecutionRequestDtoRepository {

    List<ExecutionRequestDto> getAll();

    Optional<ExecutionRequestDto> getById(UUID uuid);

}
