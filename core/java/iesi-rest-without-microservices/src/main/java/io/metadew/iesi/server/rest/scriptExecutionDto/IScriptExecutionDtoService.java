package io.metadew.iesi.server.rest.scriptExecutionDto;

import java.util.Optional;

public interface IScriptExecutionDtoService {

    Optional<ScriptExecutionDto> getByRunIdAndProcessId(String runId, Long processId);

}
