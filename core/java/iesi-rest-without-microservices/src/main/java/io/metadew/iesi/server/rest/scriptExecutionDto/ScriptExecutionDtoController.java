package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "script-executions", description = "Everything about script_executions")
@RestController
@RequestMapping("/script-executions")
@ConditionalOnWebApplication
public class ScriptExecutionDtoController {

    IScriptExecutionDtoService scriptExecutionService;

    @Autowired
    ScriptExecutionDtoController(IScriptExecutionDtoService scriptExecutionService) {
        this.scriptExecutionService = scriptExecutionService;
    }

    @GetMapping
    @PreAuthorize("hasPrivilege('SCRIPT_EXECUTIONS_READ')")
    public HalMultipleEmbeddedResource<ScriptExecutionDto> getAll() {
        return new HalMultipleEmbeddedResource<>(
                scriptExecutionService.getAll(SecurityContextHolder.getContext().getAuthentication()));
    }

    @GetMapping("/{runId}")
    @PreAuthorize("hasPrivilege('SCRIPT_EXECUTIONS_READ')")
    public HalMultipleEmbeddedResource<ScriptExecutionDto> getByRunId(@PathVariable String runId) {
        return new HalMultipleEmbeddedResource<>(
                scriptExecutionService.getByRunId(SecurityContextHolder.getContext().getAuthentication(), runId));
    }

    @GetMapping("/{runId}/{processId}")
    @PreAuthorize("hasPrivilege('SCRIPT_EXECUTIONS_READ')")
    @PostAuthorize("hasPrivilege('SCRIPT_EXECUTIONS_READ', returnObject.securityGroupName)")
    public ScriptExecutionDto getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId) {
        return scriptExecutionService
                .getByRunIdAndProcessId(
                        null,
                        runId,
                        processId)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptExecutionKey(runId)));
    }

}
