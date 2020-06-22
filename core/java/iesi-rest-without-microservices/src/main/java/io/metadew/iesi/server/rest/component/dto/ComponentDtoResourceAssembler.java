package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.server.rest.component.ComponentsController;
import io.metadew.iesi.server.rest.component.dto.ComponentAttributeDto;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentParameterDto;
import io.metadew.iesi.server.rest.component.dto.ComponentVersionDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentDtoResourceAssembler extends RepresentationModelAssemblerSupport<Component, ComponentDto> {

    public ComponentDtoResourceAssembler() {
        super(ComponentsController.class, ComponentDto.class);
    }

    @Override
    public ComponentDto toModel(Component component) {
        ComponentDto componentDto = convertToDto(component);
        Link selfLink = linkTo(methodOn(ComponentsController.class).get(component.getName(),
                component.getVersion().getMetadataKey().getComponentKey().getVersionNumber()))
                .withRel("component:" + componentDto.getName() +"-"+ componentDto.getVersion().getNumber());
        componentDto.add(selfLink);
        Link versionLink = linkTo(methodOn(ComponentsController.class).getByName(component.getName()))
                .withRel("component");
        componentDto.add(versionLink);
        return componentDto;
    }

    private ComponentDto convertToDto(Component component) {
        return new ComponentDto(component.getType(), component.getName(), component.getDescription(),
                new ComponentVersionDto(component.getVersion().getMetadataKey().getComponentKey().getVersionNumber(), component.getVersion().getDescription()),
                component.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()),
                component.getAttributes().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ComponentParameterDto convertToDto(ComponentParameter componentParameter) {
        return new ComponentParameterDto(componentParameter.getMetadataKey().getParameterName(), componentParameter.getValue());
    }

    private ComponentAttributeDto convertToDto(ComponentAttribute componentAttribute) {
        return new ComponentAttributeDto(componentAttribute.getMetadataKey().getEnvironmentKey().getName(),
                componentAttribute.getMetadataKey().getComponentAttributeName(), componentAttribute.getValue());
    }
}