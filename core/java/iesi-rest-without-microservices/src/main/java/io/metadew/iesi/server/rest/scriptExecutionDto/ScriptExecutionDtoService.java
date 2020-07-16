package io.metadew.iesi.server.rest.scriptExecutionDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScriptExecutionDtoService implements IScriptExecutionDtoService {

    private final IScriptExecutionDtoRepository scriptExecutionDtoRepository;

    @Autowired
    ScriptExecutionDtoService(IScriptExecutionDtoRepository scriptExecutionDtoRepository){
        this.scriptExecutionDtoRepository = scriptExecutionDtoRepository;
    }

    @Override
    public Optional<ScriptExecutionDto> getByRunIdAndProcessId(String runId, Long processId) {
        return scriptExecutionDtoRepository.getByRunIdAndProcessId(runId, processId);
    }

}
