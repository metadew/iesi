package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.scriptExecutionDto.ScriptExecutionDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IExecutionRequestDtoRepository {

    List<ExecutionRequestDto> getAll();

    List<ExecutionRequestDto> getId(UUID uuid);

}
