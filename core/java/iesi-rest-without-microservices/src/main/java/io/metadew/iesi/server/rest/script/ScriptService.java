package io.metadew.iesi.server.rest.script;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.ws.policy.PolicyException;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptPolicyDefinition;
import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.audit.IScriptDesignAuditService;
import io.metadew.iesi.server.rest.script.dto.IScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
@Log4j2
public class ScriptService implements IScriptService {

    private ScriptConfiguration scriptConfiguration;
    private ScriptDesignAuditConfiguration scriptDesignAuditConfiguration;
    private IScriptDesignAuditService scriptDesignAuditPostDtoService;
    private MetadataPolicyConfiguration metadataPolicyConfiguration;

    private ScriptService(ScriptConfiguration scriptConfiguration,
                          ScriptDesignAuditConfiguration scriptDesignAuditConfiguration,
                          IScriptDesignAuditService scriptDesignAuditPostDtoService,
                          MetadataPolicyConfiguration metadataPolicyConfiguration
    ) {
        this.scriptConfiguration = scriptConfiguration;
        this.scriptDesignAuditConfiguration = scriptDesignAuditConfiguration;
        this.scriptDesignAuditPostDtoService = scriptDesignAuditPostDtoService;
        this.metadataPolicyConfiguration = metadataPolicyConfiguration;
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

    public void createScript(Script script) {
        metadataPolicyConfiguration.verifyScriptPolicies(script);
        scriptConfiguration.insert(script);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(script, ScriptDesignAuditAction.CREATE));
    }

    @Override
    public List<Script> importScripts(String textPlain) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(textPlain);

        return dataObjectOperation.getDataObjects().stream().map((dataObject -> {
            Script script = (Script) objectMapper.convertValue(dataObject, Metadata.class);
            if (ScriptConfiguration.getInstance().exists(script.getMetadataKey())) {
                log.info(MessageFormat.format("Script {0} already exists in design repository. Updating to new definition", script.getName()));
                this.updateScript(script);
            } else {
                this.createScript(script);
            }

            return script;
        })).collect(Collectors.toList());
    }

    public void updateScript(Script script) {
        scriptConfiguration.update(script);
        scriptDesignAuditConfiguration.insert(scriptDesignAuditPostDtoService.convertToScriptAudit(script, ScriptDesignAuditAction.UPDATE));
    }

    public void updateScripts(List<Script> scripts) {
        scripts.forEach(this::updateScript);
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
