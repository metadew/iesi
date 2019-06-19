package io.metadew.iesi.server.rest.resource.component.resource;

import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentVersionDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentDtoResourceAssembler extends ResourceAssemblerSupport<Component, ComponentDto> {

    private final ModelMapper modelMapper;

    public ComponentDtoResourceAssembler() {
        super(ComponentsController.class, ComponentDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ComponentDto toResource(Component component) {
        ComponentDto componentDto = convertToDto(component);
        Link selfLink = linkTo(methodOn(ComponentsController.class).getComponentsAndVersion(component.getName(),
                component.getVersion().getNumber()))
                .withRel("component:" + componentDto.getName() +"-"+ componentDto.getVersion().getNumber());
        componentDto.add(selfLink);
        Link versionLink = linkTo(methodOn(ComponentsController.class).getByName(component.getName()))
                .withRel("component");
        componentDto.add(versionLink);
        return componentDto;
    }


    private ComponentDto convertToDto(Component component) {
        return new ComponentDto(component.getType(), component.getName(), component.getDescription(),
                ComponentVersionDto.convertToDto(component.getVersion()), component.getParameters(), component.getAttributes());
    }
}