package io.metadew.iesi.server.rest.scriptExecutionDto;

import java.util.List;
import java.util.Optional;

public interface IScriptExecutionDtoRepository {

    List<ScriptExecutionDto> getAll();

    List<ScriptExecutionDto> getByRunId(String runId);

    Optional<ScriptExecutionDto> getByRunIdAndProcessId(String runId, Long processId);

}
