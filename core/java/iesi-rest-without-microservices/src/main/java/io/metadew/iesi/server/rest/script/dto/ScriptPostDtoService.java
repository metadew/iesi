package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.script.dto.action.IScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.IScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.IScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ScriptPostDtoService implements IScriptPostDtoService {

    private IScriptParameterDtoService scriptParameterDtoService;
    private IScriptLabelDtoService scriptLabelDtoService;
    private IScriptActionDtoService scriptActionDtoService;
    private IScriptVersionDtoService scriptVersionDtoService;
    private SecurityGroupConfiguration securityGroupConfiguration;

    @Autowired
    public ScriptPostDtoService(IScriptParameterDtoService scriptParameterDtoService, IScriptLabelDtoService scriptLabelDtoService,
                                IScriptActionDtoService scriptActionDtoService, IScriptVersionDtoService scriptVersionDtoService,
                                SecurityGroupConfiguration securityGroupConfiguration) {
        this.scriptActionDtoService = scriptActionDtoService;
        this.scriptLabelDtoService = scriptLabelDtoService;
        this.scriptParameterDtoService = scriptParameterDtoService;
        this.scriptVersionDtoService = scriptVersionDtoService;
        this.securityGroupConfiguration = securityGroupConfiguration;
    }

    public Script convertToEntity(ScriptPostDto scriptDto) {
        SecurityGroup securityGroup = securityGroupConfiguration.getByName(scriptDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("could not find Security Group with name " + scriptDto.getSecurityGroupName()));
        return new Script(
                new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()),
                        scriptVersionDtoService.convertToEntity(scriptDto.getVersion(), IdentifierTools.getScriptIdentifier(scriptDto.getName())).getNumber()),
                securityGroup.getMetadataKey(),
                scriptDto.getSecurityGroupName(),
                scriptDto.getName(),
                scriptDto.getDescription(),
                scriptVersionDtoService.convertToEntity(scriptDto.getVersion(), IdentifierTools.getScriptIdentifier(scriptDto.getName())),
                scriptDto.getParameters().stream()
                        .map(parameter -> parameter.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber())))
                        .collect(Collectors.toList()),
                scriptDto.getActions().stream()
                        .map(action -> action.convertToEntity(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber()))
                        .collect(Collectors.toList()),
                scriptDto.getLabels().stream()
                        .map(label -> label.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptDto.getName()), scriptDto.getVersion().getNumber())))
                        .collect(Collectors.toList()));
    }

    public ScriptPostDto convertToDto(Script script) {
        return new ScriptPostDto(script.getName(),
                script.getSecurityGroupName(),
                script.getDescription(),
                scriptVersionDtoService.convertToDto(script.getVersion()),
                script.getParameters().stream().map(scriptParameterDtoService::convertToDto).collect(Collectors.toList()),
                script.getActions().stream().map(scriptActionDtoService::convertToDto).collect(Collectors.toList()),
                script.getLabels().stream().map(scriptLabelDtoService::convertToDto).collect(Collectors.toList()));
    }

}
