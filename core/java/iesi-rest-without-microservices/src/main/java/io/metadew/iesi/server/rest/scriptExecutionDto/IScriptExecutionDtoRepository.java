package io.metadew.iesi.server.rest.scriptExecutionDto;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IScriptExecutionDtoRepository {

    List<ScriptExecutionDto> getAll(Authentication authentication);

    List<ScriptExecutionDto> getByRunId(Authentication authentication, String runId);

    Optional<ScriptExecutionDto> getByRunIdAndProcessId(Authentication authentication, String runId, Long processId);

}
