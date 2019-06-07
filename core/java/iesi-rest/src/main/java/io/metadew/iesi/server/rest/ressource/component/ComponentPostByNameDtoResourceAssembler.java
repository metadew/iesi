package io.metadew.iesi.server.rest.ressource.component;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.ressource.component.ComponentPostByNameDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentPostByNameDtoResourceAssembler extends ResourceAssemblerSupport<List<Component>, ComponentPostByNameDto> {

    private final ModelMapper modelMapper;

    public ComponentPostByNameDtoResourceAssembler() {
        super(ComponentsController.class, ComponentPostByNameDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ComponentPostByNameDto toResource(List<Component> components) {
        ComponentPostByNameDto componentByNameDto = convertToDto(components);
        componentByNameDto.add(linkTo(methodOn(ComponentsController.class)
                .getByName(componentByNameDto.getName()))
                .withSelfRel());
        return componentByNameDto;
    }

    private ComponentPostByNameDto convertToDto(List<Component> components) {

        ComponentPostByNameDto connectionByNameDto = modelMapper.map(components.get(0), ComponentPostByNameDto.class);
//
        return connectionByNameDto;
    }
}

