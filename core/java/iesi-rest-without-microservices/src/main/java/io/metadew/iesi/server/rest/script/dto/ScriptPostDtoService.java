package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.dto.action.IScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.IScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.IScriptParameterDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class ScriptPostDtoService implements IScriptPostDtoService {

    private IScriptParameterDtoService scriptParameterDtoService;
    private IScriptLabelDtoService scriptLabelDtoService;
    private IScriptActionDtoService scriptActionDtoService;
    private SecurityGroupConfiguration securityGroupConfiguration;

    @Autowired
    public ScriptPostDtoService(IScriptParameterDtoService scriptParameterDtoService, IScriptLabelDtoService scriptLabelDtoService,
                                IScriptActionDtoService scriptActionDtoService,
                                SecurityGroupConfiguration securityGroupConfiguration) {
        this.scriptActionDtoService = scriptActionDtoService;
        this.scriptLabelDtoService = scriptLabelDtoService;
        this.scriptParameterDtoService = scriptParameterDtoService;
        this.securityGroupConfiguration = securityGroupConfiguration;
    }


    public ScriptVersion convertToEntity(ScriptPostDto scriptPostDto) {
        SecurityGroup securityGroup = securityGroupConfiguration.getByName(scriptPostDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("could not find Security Group with name " + scriptPostDto.getSecurityGroupName()));
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptPostDto.getName())),
                scriptPostDto.getVersion().getNumber(), "NA");

        return new ScriptVersion(
                scriptVersionKey,
                new Script(
                        new ScriptKey(IdentifierTools.getScriptIdentifier(scriptPostDto.getName())),
                        securityGroup.getMetadataKey(),
                        scriptPostDto.getSecurityGroupName(),
                        scriptPostDto.getName(),
                        scriptPostDto.getDescription(),
                        "NA"),
                scriptPostDto.getVersion().getDescription(),
                scriptPostDto.getParameters().stream()
                        .map(parameter -> parameter.convertToEntity(scriptVersionKey))
                        .collect(Collectors.toSet()),
                scriptPostDto.getActions().stream()
                        .map(action -> action.convertToEntity(scriptVersionKey))
                        .collect(Collectors.toSet()),
                scriptPostDto.getLabels().stream()
                        .map(label -> label.convertToEntity(scriptVersionKey))
                        .collect(Collectors.toSet()),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                LocalDateTime.now().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                LocalDateTime.now().toString()
        );
    }

}
