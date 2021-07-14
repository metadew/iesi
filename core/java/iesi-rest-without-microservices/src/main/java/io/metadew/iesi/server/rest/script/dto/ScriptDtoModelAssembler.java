package io.metadew.iesi.server.rest.script.dto;


import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.server.rest.script.ScriptsController;
import io.metadew.iesi.server.rest.script.dto.action.IScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.IScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.IScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class ScriptDtoModelAssembler extends RepresentationModelAssemblerSupport<ScriptVersion, ScriptDto> {

    private final IScriptParameterDtoService scriptParameterDtoService;
    private final IScriptLabelDtoService scriptLabelDtoService;
    private final IScriptActionDtoService scriptActionDtoService;

    @Autowired
    public ScriptDtoModelAssembler(IScriptParameterDtoService scriptParameterDtoService, IScriptLabelDtoService scriptLabelDtoService, IScriptActionDtoService scriptActionDtoService) {
        super(ScriptsController.class, ScriptDto.class);
        this.scriptParameterDtoService = scriptParameterDtoService;
        this.scriptLabelDtoService = scriptLabelDtoService;
        this.scriptActionDtoService = scriptActionDtoService;
    }

    public ScriptDto convertToDto(ScriptVersion scriptVersion) {
        return new ScriptDto(scriptVersion.getScript().getName(),
                scriptVersion.getScript().getSecurityGroupName(),
                scriptVersion.getDescription(),
                new ScriptVersionDto(
                        scriptVersion.getNumber(),
                        scriptVersion.getDescription(),
                        scriptVersion.getMetadataKey().getDeletedAt()
                ),
                scriptVersion.getParameters().stream().map(scriptParameterDtoService::convertToDto).collect(Collectors.toSet()),
                scriptVersion.getActions().stream().map(scriptActionDtoService::convertToDto).collect(Collectors.toSet()),
                scriptVersion.getLabels().stream().map(scriptLabelDtoService::convertToDto).collect(Collectors.toSet()),
                null,
                null);
    }

    @Override
    public ScriptDto toModel(ScriptVersion scriptVersion) {
        return convertToDto(scriptVersion);
    }

    public ScriptDto toModel(ScriptDto scriptDto) {
        return scriptDto;
    }

}
