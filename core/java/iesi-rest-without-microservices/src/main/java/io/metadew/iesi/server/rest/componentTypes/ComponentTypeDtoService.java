package io.metadew.iesi.server.rest.componentTypes;

import io.metadew.iesi.metadata.definition.component.ComponentType;
import io.metadew.iesi.server.rest.componentTypes.parameter.ComponentTypeParameterDtoService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ComponentTypeDtoService implements IComponentTypeDtoService {

    private ComponentTypeParameterDtoService componentTypeParameterDtoService;

    public ComponentTypeDtoService(ComponentTypeParameterDtoService componentTypeParameterDtoService) {
        this.componentTypeParameterDtoService = componentTypeParameterDtoService;
    }

    public ComponentTypeDto convertToDto(ComponentType componentType, String name) {
        return new ComponentTypeDto(name, componentType.getDescription(),
                componentType.getParameters().entrySet().stream()
                        .map(entry -> componentTypeParameterDtoService.convertToDto(entry.getValue(), entry.getKey()))
                        .collect(Collectors.toList()));
    }
}
