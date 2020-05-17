package io.metadew.iesi.server.rest.script.resource;


import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.script.ScriptController;
import io.metadew.iesi.server.rest.script.dto.IScriptDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptDtoResourceAssembler extends RepresentationModelAssemblerSupport<Script, ScriptDto> {

    private IScriptDtoService scriptDtoService;

    @Autowired
    public ScriptDtoResourceAssembler(IScriptDtoService scriptDtoService) {
        super(ScriptController.class, ScriptDto.class);
        this.scriptDtoService = scriptDtoService;
    }

    @Override
    public ScriptDto toModel(Script script) {
        ScriptDto scriptDto = scriptDtoService.convertToDto(script);
        Link selfLink = linkTo(methodOn(ScriptController.class).get(script.getName(), script.getVersion().getNumber(), null))
                .withRel("script:" + scriptDto.getName() + "-" + scriptDto.getVersion().getNumber());
        scriptDto.add(selfLink);
//        Link versionLink = linkTo(methodOn(ScriptController.class).executeScript(null, scriptByNameDto.getName(), scriptByNameDto.getVersion().getNumber()))
//                .withRel("script");
//        scriptByNameDto.add(versionLink);
        return scriptDto;
    }

}