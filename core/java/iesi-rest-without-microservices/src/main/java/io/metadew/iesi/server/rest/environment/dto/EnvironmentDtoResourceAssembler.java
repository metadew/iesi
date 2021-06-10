package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.server.rest.environment.EnvironmentsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public  class EnvironmentDtoResourceAssembler extends RepresentationModelAssemblerSupport<Environment, EnvironmentDto> {

    private final ModelMapper modelMapper;

    public EnvironmentDtoResourceAssembler() {
        super(EnvironmentsController.class, EnvironmentDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public EnvironmentDto toModel(Environment environment) {
        EnvironmentDto environmentDto = convertToDto(environment);
        Link selfLink = linkTo(methodOn(EnvironmentsController.class).getByName(environmentDto.getName()))
                .withSelfRel();
        environmentDto.add(selfLink);
        return environmentDto;
    }

    private EnvironmentDto convertToDto(Environment environment) {
        return new EnvironmentDto(environment.getName(), environment.getDescription(),
                environment.getParameters()
                        .stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()));
    }

    private EnvironmentParameterDto convertToDto(EnvironmentParameter environmentParameter) {
        return new EnvironmentParameterDto(environmentParameter.getName(), environmentParameter.getValue());
    }
}