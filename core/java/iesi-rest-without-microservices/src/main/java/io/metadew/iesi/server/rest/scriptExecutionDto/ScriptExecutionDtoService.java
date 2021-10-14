package io.metadew.iesi.server.rest.scriptExecutionDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnWebApplication
public class ScriptExecutionDtoService implements IScriptExecutionDtoService {

    private final IScriptExecutionDtoRepository scriptExecutionDtoRepository;

    @Autowired
    ScriptExecutionDtoService(IScriptExecutionDtoRepository scriptExecutionDtoRepository) {
        this.scriptExecutionDtoRepository = scriptExecutionDtoRepository;
    }

    public List<ScriptExecutionDto> getAll(Authentication authentication) {
        return scriptExecutionDtoRepository.getAll(authentication);
    }

    @Override
    public List<ScriptExecutionDto> getByRunId(Authentication authentication, String runId) {
        return scriptExecutionDtoRepository.getByRunId(authentication, runId);
    }

    @Override
    public Optional<ScriptExecutionDto> getByRunIdAndProcessId(Authentication authentication, String runId, Long processId) {
        return scriptExecutionDtoRepository.getByRunIdAndProcessId(authentication, runId, processId);
    }

}
