package io.metadew.iesi.server.rest.script.execution;

public interface IScriptExecutionDtoService {

    ScriptExecutionDto getByRunIdAndProcessId(String runId, String processId);

}
