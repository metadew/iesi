package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.server.rest.script.result.dto.ScriptResultDto;
import io.metadew.iesi.server.rest.script.result.dto.ScriptResultDtoModelAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "scriptResults", description = "Everything about scriptResults")
@RequestMapping("/scriptResult")
public class ScriptResultController {
    private final IScriptResultService scriptResultService;
    private final ScriptResultDtoModelAssembler scriptResultDtoModelAssembler;

    @Autowired
    public ScriptResultController(IScriptResultService scriptResultService, ScriptResultDtoModelAssembler scriptResultDtoModelAssembler) {
        this.scriptResultService = scriptResultService;
        this.scriptResultDtoModelAssembler = scriptResultDtoModelAssembler;
    }

    @GetMapping
    public List<ScriptResultDto> getAll() {
        return scriptResultService.getAll().stream()
                .map(ScriptResultDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{runId}")
    public List<ScriptResultDto> getByRunId(@PathVariable String runId) {
        return scriptResultService.getByRunId(runId).stream()
                .map(ScriptResultDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{runId}/{processId}")
    public ScriptResultDto getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId) {
        return scriptResultService.getByRunIdAndProcessId(runId, processId)
                .map(ScriptResultDto::new)
                .map(scriptResultDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, processId)));
    }

}
