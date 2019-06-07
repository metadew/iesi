package io.metadew.iesi.server.rest.ressource.component;


import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;


import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentGetByNameGetDtoAssembler extends ResourceAssemblerSupport<List<Component>, ComponentGetByNameDto> {

    private final ModelMapper modelMapper;

    public ComponentGetByNameGetDtoAssembler() {
        super(ComponentsController.class, ComponentGetByNameDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ComponentGetByNameDto toResource(List<Component> components) {
        ComponentGetByNameDto componentGlobalDto = convertToDto(components);
        componentGlobalDto.add(linkTo(methodOn(ComponentsController.class)
                .getByName(componentGlobalDto.getName()))
                .withSelfRel());
        return componentGlobalDto;
    }

    private ComponentGetByNameDto convertToDto(List<Component> components) {

        return modelMapper.map(components.get(0), ComponentGetByNameDto.class);

    }
}
