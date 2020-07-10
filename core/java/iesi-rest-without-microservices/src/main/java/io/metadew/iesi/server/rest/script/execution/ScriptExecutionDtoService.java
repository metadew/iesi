package io.metadew.iesi.server.rest.script.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScriptExecutionDtoService implements IScriptExecutionDtoService {

    private final IScriptExecutionDtoRepository scriptExecutionDtoRepository;

    @Autowired
    ScriptExecutionDtoService(IScriptExecutionDtoRepository scriptExecutionDtoRepository){
        this.scriptExecutionDtoRepository = scriptExecutionDtoRepository;
    }

    @Override
    public ScriptExecutionDto getByRunIdAndProcessId(String runId, String processId) {
        return scriptExecutionDtoRepository.getByRunIdAndProcessId(runId, processId);
    }

}
