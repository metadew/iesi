package io.metadew.iesi.server.rest.resource.component.resource;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.server.rest.controller.ComponentsController;
import io.metadew.iesi.server.rest.resource.component.dto.ComponentGlobalDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.stereotype.Component
public class ComponentGlobalDtoResourceAssembler extends RepresentationModelAssemblerSupport<List<Component>, ComponentGlobalDto> {

        private final ModelMapper modelMapper;

        public ComponentGlobalDtoResourceAssembler () {
            super(ComponentsController.class, ComponentGlobalDto.class);
            this.modelMapper = new ModelMapper();
        }

        @Override
        public ComponentGlobalDto toModel(List<Component> components) {
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



