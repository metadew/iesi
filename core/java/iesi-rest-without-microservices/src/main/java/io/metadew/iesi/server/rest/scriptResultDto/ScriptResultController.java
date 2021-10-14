package io.metadew.iesi.server.rest.scriptResultDto;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.scriptResultDto.dto.ScriptResultDto;
import io.metadew.iesi.server.rest.scriptResultDto.dto.ScriptResultDtoModelAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Tag(name = "scriptResults", description = "Everything about scriptResults")
@RequestMapping("/script-results")
@ConditionalOnWebApplication
public class ScriptResultController {
    private final IScriptResultService scriptResultService;
    private final ScriptResultDtoModelAssembler scriptResultDtoModelAssembler;

    @Autowired
    public ScriptResultController(IScriptResultService scriptResultService, ScriptResultDtoModelAssembler scriptResultDtoModelAssembler) {
        this.scriptResultService = scriptResultService;
        this.scriptResultDtoModelAssembler = scriptResultDtoModelAssembler;
    }

    @GetMapping
    public HalMultipleEmbeddedResource<ScriptResultDto> getAll() {
        return new HalMultipleEmbeddedResource<>(scriptResultService.getAll().stream()
                .map(scriptResultDtoModelAssembler::toModel)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{runId}")
    @PreAuthorize("hasPrivilege('SCRIPT_RESULTS_READ')")
    public HalMultipleEmbeddedResource<ScriptResultDto> getByRunId(@PathVariable String runId) {
        return new HalMultipleEmbeddedResource<>(scriptResultService.getByRunId(runId).stream()
                .map(scriptResultDtoModelAssembler::toModel)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{runId}/{processId}")
    @PreAuthorize("hasPrivilege('SCRIPT_RESULTS_READ')")
    public ScriptResultDto getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId) throws MetadataDoesNotExistException {
        return scriptResultService.getByRunIdAndProcessId(runId, processId)
                .map(scriptResultDtoModelAssembler::toModel)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, processId)));
    }

}
