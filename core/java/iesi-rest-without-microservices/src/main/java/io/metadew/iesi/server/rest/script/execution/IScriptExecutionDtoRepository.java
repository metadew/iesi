package io.metadew.iesi.server.rest.script.execution;

public interface IScriptExecutionDtoRepository {

    ScriptExecutionDto getByRunIdAndProcessId(String runId, String processId);

}
