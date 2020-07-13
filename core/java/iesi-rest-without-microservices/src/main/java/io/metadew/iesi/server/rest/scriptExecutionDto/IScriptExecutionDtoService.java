package io.metadew.iesi.server.rest.scriptExecutionDto;

public interface IScriptExecutionDtoService {

    ScriptExecutionDto getByRunIdAndProcessId(String runId, Long processId);

}
