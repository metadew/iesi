package io.metadew.iesi.server.rest.scriptExecutionDto;

public interface IScriptExecutionDtoRepository {

    ScriptExecutionDto getByRunIdAndProcessId(String runId, String processId);

}
