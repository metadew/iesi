package io.metadew.iesi.server.rest.resource.component.resource;

import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentGlobalDto;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.modelmapper.ModelMapper;

import io.metadew.iesi.metadata.definition.Component;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Component>, ComponentGlobalDto>{

        private final ModelMapper modelMapper;

        public ComponentGlobalDtoResourceAssembler () {
            super(ComponentsController.class, ComponentGlobalDto.class);
            this.modelMapper = new ModelMapper();
        }

        @Override
        public ComponentGlobalDto toResource(List<Component> components) {
            ComponentGlobalDto componentGlobalDto = convertToDto(components);
            componentGlobalDto.add(linkTo(methodOn(ComponentsController.class)
                    .getByName(componentGlobalDto.getName()))
                    .withSelfRel());
            return componentGlobalDto;
        }
        private ComponentGlobalDto convertToDto(List<Component> components) {

            return modelMapper.map(components.get(0), ComponentGlobalDto.class);

        }
}

