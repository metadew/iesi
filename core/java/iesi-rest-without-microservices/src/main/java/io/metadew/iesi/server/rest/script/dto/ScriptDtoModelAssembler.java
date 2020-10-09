package io.metadew.iesi.server.rest.script.dto;


import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.script.ScriptsController;
import io.metadew.iesi.server.rest.script.dto.action.IScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.IScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.IScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.IScriptVersionDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptDtoModelAssembler extends RepresentationModelAssemblerSupport<Script, ScriptDto> {

    private final IScriptParameterDtoService scriptParameterDtoService;
    private final IScriptLabelDtoService scriptLabelDtoService;
    private final IScriptActionDtoService scriptActionDtoService;
    private final IScriptVersionDtoService scriptVersionDtoService;

    @Autowired
    public ScriptDtoModelAssembler(IScriptParameterDtoService scriptParameterDtoService, IScriptLabelDtoService scriptLabelDtoService, IScriptActionDtoService scriptActionDtoService, IScriptVersionDtoService scriptVersionDtoService) {
        super(ScriptsController.class, ScriptDto.class);
        this.scriptParameterDtoService = scriptParameterDtoService;
        this.scriptLabelDtoService = scriptLabelDtoService;
        this.scriptActionDtoService = scriptActionDtoService;
        this.scriptVersionDtoService = scriptVersionDtoService;
    }

    public ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getName(),
                script.getSecurityGroupName(),
                script.getDescription(),
                scriptVersionDtoService.convertToDto(script.getVersion()),
                script.getParameters().stream().map(scriptParameterDtoService::convertToDto).collect(Collectors.toSet()),
                script.getActions().stream().map(scriptActionDtoService::convertToDto).collect(Collectors.toSet()),
                script.getLabels().stream().map(scriptLabelDtoService::convertToDto).collect(Collectors.toSet()),
                null,
                null);
    }

    @Override
    public ScriptDto toModel(Script script) {
        return toModel(convertToDto(script));
    }

    public ScriptDto toModel(ScriptDto scriptDto) {
        addLinksToExistingDto(scriptDto);
        return scriptDto;
    }

    public void addLinksToExistingDto(ScriptDto scriptDto) {
        Link selfLink = linkTo(methodOn(ScriptsController.class).get(scriptDto.getName(), scriptDto.getVersion().getNumber(), new ArrayList<>()))
                .withSelfRel();
        Link linkToAllVersionOfTheScript = linkTo(methodOn(ScriptsController.class).getByName(PageRequest.of(0, 20), scriptDto.getName(), new ArrayList<>(), ""))
                .withRel("AllVersionOfTheScript");
        Link linkToAll = linkTo(methodOn(ScriptsController.class).getAll(PageRequest.of(0, 20), new ArrayList<>(), "", null, null))
                .withRel("scripts");
        scriptDto.add(selfLink, linkToAllVersionOfTheScript, linkToAll);
    }

}
