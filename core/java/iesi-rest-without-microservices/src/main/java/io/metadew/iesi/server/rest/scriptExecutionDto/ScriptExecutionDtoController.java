package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "script_executions", description = "Everything about script_executions")
@RestController
@RequestMapping("/script_executions")
@CrossOrigin
public class ScriptExecutionDtoController {

    IScriptExecutionDtoService scriptExecutionService;

    @Autowired
    ScriptExecutionDtoController(IScriptExecutionDtoService scriptExecutionService) {
        this.scriptExecutionService = scriptExecutionService;
    }

    @GetMapping
    public HalMultipleEmbeddedResource<ScriptExecutionDto> getAll() {
        return new HalMultipleEmbeddedResource<>(scriptExecutionService.getAll());
    }

    @GetMapping("/{runId}")
    public HalMultipleEmbeddedResource<ScriptExecutionDto> getByRunId(@PathVariable String runId) {
        return new HalMultipleEmbeddedResource<>(scriptExecutionService.getByRunId(runId));
    }

    @GetMapping("/{runId}/{processId}")
    public ScriptExecutionDto getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId) {
        return scriptExecutionService.getByRunIdAndProcessId(runId, processId)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptExecutionKey(runId)));
    }

}
