package io.metadew.iesi.server.rest.ressource.environment;

import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.EnvironmentParameter;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EnvironmentByNameDtoResourceAssembler extends ResourceAssemblerSupport<List<Environment>, EnvironmentByNameDto> {

    private final ModelMapper modelMapper;

    public EnvironmentByNameDtoResourceAssembler() {
        super(EnvironmentsController.class, EnvironmentByNameDto.class);
        this.modelMapper = new ModelMapper();
    }


    @Override
    public EnvironmentByNameDto toResource(List<Environment> environments) {
        EnvironmentByNameDto environmentByNameDto = convertToDto(environments);
        for (EnvironmentParameter environment : environmentByNameDto.getParameters()) {
            environmentByNameDto.add(linkTo(methodOn(EnvironmentsController.class).getByName(environmentByNameDto.getName()))
                    .withRel("environment:"+environmentByNameDto.getName()+"-"+environment));
        }
        return environmentByNameDto;
    }

    private EnvironmentByNameDto convertToDto(List<Environment> environments) {
        if (environments.isEmpty()) {
            throw new IllegalArgumentException("Environments have to be non empty");
        }
        if (!environments.stream().allMatch(environment -> environment.getName().equals(environments.get(0).getName()))) {
            throw new IllegalArgumentException(MessageFormat.format("Environments ''{0}'' do not define the same name ''{1}''", environments, environments.get(0).getName()));
        }


        EnvironmentByNameDto environmentByNameDto = modelMapper.map(environments.get(0), EnvironmentByNameDto.class);
//        environmentByNameDto.setParameters(environments.get(0).getParameters());
        return environmentByNameDto;
    }
}