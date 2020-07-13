package io.metadew.iesi.server.rest.script.dto;


import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.script.ScriptController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptPostDtoModelAssembler extends RepresentationModelAssemblerSupport<Script, ScriptPostDto> {

    private IScriptPostDtoService iScriptPostDtoService;

    public ScriptPostDtoModelAssembler(IScriptPostDtoService iScriptPostDtoService) {
        super(ScriptController.class, ScriptPostDto.class);
        this.iScriptPostDtoService = iScriptPostDtoService;
    }

    @Override
    public ScriptPostDto toModel(Script script) {
        ScriptPostDto scriptPostDto = iScriptPostDtoService.convertToDto(script);
        Link selfLink = linkTo(methodOn(ScriptController.class).get(script.getName(), script.getVersion().getNumber(), null))
                .withRel("script:" + scriptPostDto.getName() + "-" + scriptPostDto.getVersion().getNumber());
        scriptPostDto.add(selfLink);
//        Link versionLink = linkTo(methodOn(ScriptController.class).executeScript(null, scriptPostDto.getName(), scriptPostDto.getVersion().getNumber()))
//                .withRel("script");
//        scriptPostDto.add(versionLink);
        return scriptPostDto;
    }

}