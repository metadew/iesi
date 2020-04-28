package io.metadew.iesi.server.rest.resource.script.resource;


import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptGlobalDtoResourceAssembler extends RepresentationModelAssemblerSupport<List<Script>, ScriptGlobalDto> {

    private final ModelMapper modelMapper;

    public ScriptGlobalDtoResourceAssembler() {
        super(ScriptController.class, ScriptGlobalDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ScriptGlobalDto toModel(List<Script> scripts) {
        ScriptGlobalDto scriptGlobalDto = convertToDto(scripts);
        scriptGlobalDto.add(linkTo(methodOn(ScriptController.class)
                .getByName(scriptGlobalDto.getName()))
                .withSelfRel());
        return scriptGlobalDto;
    }

    private ScriptGlobalDto convertToDto(List<Script> scripts) {
        return new ScriptGlobalDto(scripts.get(0).getName(), scripts.get(0).getDescription());

    }
}
