package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.dto.IScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScriptService implements IScriptService {

    private ScriptConfiguration scriptConfiguration;
    private IScriptPostDtoService scriptPostDtoService;

    private ScriptService(ScriptConfiguration scriptConfiguration,
                          IScriptPostDtoService scriptPostDtoService) {
        this.scriptConfiguration = scriptConfiguration;
        this.scriptPostDtoService = scriptPostDtoService;
    }

    public List<Script> getAll() {
        return scriptConfiguration.getAll();
    }

    public List<Script> getByName(String name) {
        return scriptConfiguration.getByName(name);
    }

    public Optional<Script> getByNameAndVersion(String name, long version) {
        return scriptConfiguration.get(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version));
    }

    public void createScript(ScriptPostDto scriptPostDto) {
        scriptConfiguration.insert(scriptPostDtoService.convertToEntity(scriptPostDto));
    }

    public void updateScript(ScriptPostDto scriptPostDto) {
        scriptConfiguration.update(scriptPostDtoService.convertToEntity(scriptPostDto));
    }

    public void updateScripts(List<ScriptPostDto> scriptPostDtos) {
        scriptPostDtos.forEach(this::updateScript);
    }

    public void deleteAll() {
    }

    public void deleteByName(String name) {
        scriptConfiguration.deleteByName(name);
    }

    @Override
    public void deleteByNameAndVersion(String name, long version) {
        scriptConfiguration.delete(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version));
    }

}
