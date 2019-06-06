package io.metadew.iesi.server.rest.ressource.script;


import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptByNameDtoResourceAssembler extends ResourceAssemblerSupport<List<Script>, ScriptByNameDto> {

    private final ModelMapper modelMapper;

    public ScriptByNameDtoResourceAssembler() {
        super(ScriptController.class, ScriptByNameDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ScriptByNameDto toResource(List<Script> scripts) {
        ScriptByNameDto scriptByNameDto = convertToDto(scripts);
        scriptByNameDto.add(linkTo(methodOn(ScriptController.class)
                .getByNameScript(scriptByNameDto.getName()))
                .withSelfRel());
        return scriptByNameDto;
    }

    private ScriptByNameDto convertToDto(List<Script> scripts) {


        ScriptByNameDto connectionByNameDto = modelMapper.map(scripts.get(0), ScriptByNameDto.class);
//
        return connectionByNameDto;
    }
}