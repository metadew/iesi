package io.metadew.iesi.server.rest.scriptExecutionDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/script_executions")
public class ScriptExecutionDtoController {

    IScriptExecutionDtoService scriptExecutionService;

    @Autowired
    ScriptExecutionDtoController(IScriptExecutionDtoService scriptExecutionService){
        this.scriptExecutionService = scriptExecutionService;
    }

    @GetMapping("/{runId}/{processId}")
    public ScriptExecutionDto getByRunIdAndProcessId(@PathVariable String runId, @PathVariable Long processId){
        return scriptExecutionService.getByRunIdAndProcessId(runId,processId);
    }

}
