package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.script_execution.dto.ScriptExecutionDto;
import io.metadew.iesi.server.rest.resource.script_execution.resource.ScriptExecutionDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/script_execution")
public class ScriptExecutionController {

    private final ScriptExecutionDtoResourceAssembler scriptExecutionDtoResourceAssembler;
    private final ScriptExecutionConfiguration scriptExecutionConfiguration;

    @Autowired
    ScriptExecutionController(ScriptExecutionConfiguration scriptExecutionConfiguration,
                              ScriptExecutionDtoResourceAssembler scriptExecutionDtoResourceAssembler) {
        this.scriptExecutionConfiguration = scriptExecutionConfiguration;
        this.scriptExecutionDtoResourceAssembler = scriptExecutionDtoResourceAssembler;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ScriptExecutionDto> getAll(@RequestParam(required = false) String scriptExecutionRequestId) {
        List<ScriptExecution> scriptExecutions;
        if (scriptExecutionRequestId != null) {
            scriptExecutions = scriptExecutionConfiguration.getByScriptExecutionRequest(new ScriptExecutionRequestKey(scriptExecutionRequestId));
        } else {
            scriptExecutions = scriptExecutionConfiguration.getAll();
        }
        return new HalMultipleEmbeddedResource<>(scriptExecutions
                .stream()
                .parallel()
                .map(scriptExecutionDtoResourceAssembler::toResource)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ScriptExecutionDto getById(@PathVariable String id) {
        return scriptExecutionConfiguration.get(new ScriptExecutionKey(id))
                .map(scriptExecutionDtoResourceAssembler::toResource)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find ExecutionRequest {0}", id)));
    }

}