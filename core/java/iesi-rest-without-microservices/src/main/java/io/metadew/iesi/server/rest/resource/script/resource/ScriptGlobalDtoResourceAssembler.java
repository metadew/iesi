package io.metadew.iesi.server.rest.resource.script.resource;


import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Script>, ScriptGlobalDto> {

    private final ModelMapper modelMapper;

    public ScriptGlobalDtoResourceAssembler() {
        super(ScriptController.class, ScriptGlobalDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ScriptGlobalDto toResource(List<Script> scripts) {
        ScriptGlobalDto scriptGlobalDto = convertToDto(scripts);
        scriptGlobalDto.add(linkTo(methodOn(ScriptController.class)
                .getByNameScript(scriptGlobalDto.getName()))
                .withSelfRel());
        return scriptGlobalDto;
    }

    private ScriptGlobalDto convertToDto(List<Script> scripts) {

        return modelMapper.map(scripts.get(0), ScriptGlobalDto.class);

    }
}
