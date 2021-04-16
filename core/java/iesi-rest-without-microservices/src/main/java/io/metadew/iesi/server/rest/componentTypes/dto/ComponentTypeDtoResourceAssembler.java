package io.metadew.iesi.server.rest.componentTypes.dto;

import io.metadew.iesi.metadata.definition.component.ComponentType;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Log4j2
public class ComponentTypeDtoResourceAssembler extends RepresentationModelAssemblerSupport<ComponentType, ComponentTypeDto> {

    private final IComponentTypeParameterService componentTypeParameterService;

    public ComponentTypeDtoResourceAssembler(IComponentTypeParameterService componentTypeParameterService) {
        super(ComponentType.class, ComponentTypeDto.class);
        this.componentTypeParameterService = componentTypeParameterService;
    }

    @Override
    public ComponentTypeDto toModel(ComponentType componentType) {
        return convertToDto(componentType);
    }

    private ComponentTypeDto convertToDto(ComponentType componentType) {
        log.info(componentType);
        return new ComponentTypeDto(
                componentType.getName(),
                componentType.getDescription(),
                componentType.getParameters().entrySet().stream()
                        .map(entry -> componentTypeParameterService.convertToDto(entry.getValue(), entry.getKey()))
                        .collect(Collectors.toList())
        );
    }
}
