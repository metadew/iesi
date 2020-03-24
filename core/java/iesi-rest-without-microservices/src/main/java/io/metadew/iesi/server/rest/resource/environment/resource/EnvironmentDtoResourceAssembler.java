package io.metadew.iesi.server.rest.resource.environment.resource;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import io.metadew.iesi.server.rest.resource.environment.dto.EnvironmentDto;
import io.metadew.iesi.server.rest.resource.environment.dto.EnvironmentParameterDto;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EnvironmentDtoResourceAssembler extends ResourceAssemblerSupport<Environment, EnvironmentDto> {

    private final ModelMapper modelMapper;

    public EnvironmentDtoResourceAssembler() {
        super(EnvironmentsController.class, EnvironmentDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public EnvironmentDto toResource(Environment environment) {
        EnvironmentDto environmentDto = convertToDto(environment);
        Link selfLink = linkTo(methodOn(EnvironmentsController.class).getByName(environmentDto.getName()))
                .withSelfRel();
        environmentDto.add(selfLink);
        Link connectionsLink = linkTo(methodOn(EnvironmentsController.class).getConnections(environmentDto.getName()))
                .withRel("connections");
        environmentDto.add(connectionsLink);
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