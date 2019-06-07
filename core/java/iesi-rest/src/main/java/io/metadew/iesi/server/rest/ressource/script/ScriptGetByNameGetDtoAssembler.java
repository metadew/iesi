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
public class ScriptGetByNameGetDtoAssembler extends ResourceAssemblerSupport<List<Script>, ScriptGetByNameDto> {

    private final ModelMapper modelMapper;

    public ScriptGetByNameGetDtoAssembler() {
        super(ScriptController.class, ScriptGetByNameDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ScriptGetByNameDto toResource(List<Script> scripts) {
        ScriptGetByNameDto scriptGlobalDto = convertToDto(scripts);
        scriptGlobalDto.add(linkTo(methodOn(ScriptController.class)
                .getByNameScript(scriptGlobalDto.getDescription()))
                .withSelfRel());
        return scriptGlobalDto;
    }

    private ScriptGetByNameDto convertToDto(List<Script> scripts) {

        return modelMapper.map(scripts.get(0), ScriptGetByNameDto.class);

    }
}