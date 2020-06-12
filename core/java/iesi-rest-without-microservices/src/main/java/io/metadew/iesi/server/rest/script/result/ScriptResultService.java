package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

import java.util.List;
import java.util.Optional;

public class ScriptResultService implements IScriptResultService{

    @Override
    public List<ScriptResult> getAll() {
        return null;
    }

    @Override
    public Optional<List<ScriptResult>> getByRunId(String RunId) {
        return Optional.empty();
    }

    @Override
    public Optional<ScriptResult> getByRunIdAndProcessId(String runId, Long processId) {
        return Optional.empty();
    }
}
