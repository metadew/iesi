package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScriptResultService implements IScriptResultService {

    private ScriptResultConfiguration scriptResultConfiguration;

    public ScriptResultService(ScriptResultConfiguration scriptResultConfiguration) {
        this.scriptResultConfiguration = scriptResultConfiguration;
    }

    @Override
    public List<ScriptResult> getAll() {
        return scriptResultConfiguration.getAll();
    }

    @Override
    public List<ScriptResult> getByRunId(String runId) {
        return scriptResultConfiguration.getByRunId(runId);
    }

    @Override
    public Optional<ScriptResult> getByRunIdAndProcessId(String runId, Long processId) {
        return scriptResultConfiguration.get(new ScriptResultKey(runId, processId));
    }
}
