package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.metadew.iesi.server.rest.helper.Filter.distinctByKey;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EnvironmentGlobalDtoResourceAssembler extends ResourceAssemblerSupport<List<Environment>, EnvironmentGlobalDto> {

    private final ModelMapper modelMapper;

    public EnvironmentGlobalDtoResourceAssembler() {
        super(EnvironmentsController.class, EnvironmentGlobalDto.class);
        this.modelMapper = new ModelMapper();
    }

    @Override
    public EnvironmentGlobalDto toResource(List<Environment> environments) {
        EnvironmentGlobalDto environmentGlobalDto = convertToDto(environments);
        environmentGlobalDto.add(linkTo(methodOn(EnvironmentsController.class)
                .getByName(environments.get(0).getName()))
                .withSelfRel());
        return environmentGlobalDto;
    }

    private EnvironmentGlobalDto convertToDto(List<Environment> environments) {
        if (environments.isEmpty()) {
            throw new IllegalArgumentException("Cannot create Environment global DTO from empty list");
        }
        if (environments.stream().filter(distinctByKey(Environment::getName)).count() > 1) {
            throw new IllegalArgumentException("Cannot create Environment global DTO from list with multiple environment names");
        }
        return modelMapper.map(environments.get(0), EnvironmentGlobalDto.class);
//        return environments.stream()
//                .filter(distinctByKey(Environment::getName))
//                .map(environment -> modelMapper.map(environment, EnvironmentGlobalDto.class))
//                .collect(Collectors.toList());

    }
}