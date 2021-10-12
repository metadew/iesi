package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.audit.IScriptDesignAuditService;
import io.metadew.iesi.server.rest.script.dto.IScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnWebApplication
public class ScriptService implements IScriptService {

    private ScriptConfiguration scriptConfiguration;
    private IScriptPostDtoService scriptPostDtoService;
    private ScriptDesignAuditConfiguration scriptDesignAuditConfiguration;
    private IScriptDesignAuditService scriptDesignAuditPostDtoService;

    private ScriptService(ScriptConfiguration scriptConfiguration, IScriptPostDtoService scriptPostDtoService,
                          ScriptDesignAuditConfiguration scriptDesignAuditConfiguration, IScriptDesignAuditService scriptDesignAuditPostDtoService) {
        this.scriptConfiguration = scriptConfiguration;
        this.scriptPostDtoService = scriptPostDtoService;
        this.scriptDesignAuditConfiguration = scriptDesignAuditConfiguration;
        this.scriptDesignAuditPostDtoService = scriptDesignAuditPostDtoService;
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
        Script script = scriptPostDtoService.convertToEntity(scriptPostDto);
        scriptConfiguration.insert(script);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(script, ScriptDesignAuditAction.CREATE));
    }

    public void updateScript(ScriptPostDto scriptPostDto) {
        Script script = scriptPostDtoService.convertToEntity(scriptPostDto);
        scriptConfiguration.update(script);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(script, ScriptDesignAuditAction.UPDATE));
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
        ScriptKey scriptKey = new ScriptKey(IdentifierTools.getScriptIdentifier(name), version);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(scriptConfiguration.get(scriptKey).get(), ScriptDesignAuditAction.DELETE));
        scriptConfiguration.delete(scriptKey);

    }

}
