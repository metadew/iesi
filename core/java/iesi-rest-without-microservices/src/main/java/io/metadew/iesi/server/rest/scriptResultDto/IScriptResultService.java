package io.metadew.iesi.server.rest.scriptResultDto;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

import java.util.List;
import java.util.Optional;

public interface IScriptResultService {

    List<ScriptResult> getAll();

    List<ScriptResult> getByRunId(String runId);

    Optional<ScriptResult> getByRunIdAndProcessId(String runId, Long processId);

}
