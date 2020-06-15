package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "scriptResults", description = "Everything about scriptResults")
@RequestMapping("/scriptResult")
public class ScriptResultController {
    private final IScriptResultService scriptResultService;

    @Autowired
    public ScriptResultController(IScriptResultService scriptResultService) {
        this.scriptResultService = scriptResultService;
    }

    @GetMapping
    public List<ScriptResult> getAll() {
        return scriptResultService.getAll();
    }

    @GetMapping("/{runId}")
    public Optional<List<ScriptResult>> getByRunId(@PathVariable String runId) {
        return scriptResultService.getByRunId(runId);
    }

    @GetMapping("/{runId}/{processId}")
    public Optional<ScriptResult> getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId) {
        return scriptResultService.getByRunIdAndProcessId(runId, processId);
    }

}
