package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptVersionConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.audit.IScriptDesignAuditService;
import io.metadew.iesi.server.rest.script.dto.IScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ScriptService implements IScriptService {

    private final ScriptConfiguration scriptConfiguration;
    private final ScriptVersionConfiguration scriptVersionConfiguration;
    private final IScriptPostDtoService scriptPostDtoService;
    private final ScriptDesignAuditConfiguration scriptDesignAuditConfiguration;
    private final IScriptDesignAuditService scriptDesignAuditPostDtoService;

    private ScriptService(ScriptConfiguration scriptConfiguration, ScriptVersionConfiguration scriptVersionConfiguration, IScriptPostDtoService scriptPostDtoService,
                          ScriptDesignAuditConfiguration scriptDesignAuditConfiguration, IScriptDesignAuditService scriptDesignAuditPostDtoService) {
        this.scriptConfiguration = scriptConfiguration;
        this.scriptVersionConfiguration = scriptVersionConfiguration;
        this.scriptPostDtoService = scriptPostDtoService;
        this.scriptDesignAuditConfiguration = scriptDesignAuditConfiguration;
        this.scriptDesignAuditPostDtoService = scriptDesignAuditPostDtoService;
    }

    public Set<ScriptVersion> getAll() {
        return new HashSet<>(scriptVersionConfiguration.getAllActive());
    }

    public Set<ScriptVersion> getByName(String name) {
        return scriptVersionConfiguration
                .getByScriptKey(scriptConfiguration
                        .getActiveByName(name)
                        .map(Metadata::getMetadataKey)
                        .orElseThrow(RuntimeException::new));
    }

    public Optional<ScriptVersion> getByNameAndVersion(String name, long version) {
        return scriptVersionConfiguration.getByNameAndVersionAndActive(name, version);
    }

    public void createScript(ScriptPostDto scriptPostDto) {
        ScriptVersion scriptVersion = scriptPostDtoService.convertToEntity(scriptPostDto);
        scriptVersionConfiguration.insert(scriptVersion);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(scriptVersion, ScriptDesignAuditAction.CREATE));
    }

    public void updateScript(ScriptPostDto scriptPostDto) {
        ScriptVersion scriptVersion = scriptPostDtoService.convertToEntity(scriptPostDto);
        scriptVersionConfiguration.update(scriptVersion);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(scriptVersion, ScriptDesignAuditAction.UPDATE));
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
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier(name)), version, "NA");
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(scriptVersionConfiguration.get(scriptVersionKey).orElseThrow(RuntimeException::new), ScriptDesignAuditAction.DELETE));
        scriptVersionConfiguration.softDelete(new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier(name)), version, "NA"), LocalDateTime.now().toString());

    }

    @Override
    public boolean existsByNameAndVersion(String name, long version) {
        return scriptVersionConfiguration.exists(new ScriptVersionKey(
                new ScriptKey(IdentifierTools.getScriptIdentifier(name)), version, "NA"));
    }

    @Override
    public boolean existsByName(String name) {
        return scriptConfiguration.exists(new ScriptKey(IdentifierTools.getScriptIdentifier(name)));
    }

    @Override
    public boolean existsDeleted(String name) {
        return scriptConfiguration.existsDeleted(new ScriptKey(IdentifierTools.getScriptIdentifier(name)));
    }

}
