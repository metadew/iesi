package io.metadew.iesi.server.rest.componentTypes.dto;

import io.metadew.iesi.metadata.definition.component.ComponentType;
import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ComponentTypeDtoResourceAssembler extends RepresentationModelAssemblerSupport<ComponentType, ComponentTypeDto> {

    public ComponentTypeDtoResourceAssembler() {
        super(ComponentType.class, ComponentTypeDto.class);
    }

    @Override
    public ComponentTypeDto toModel(ComponentType componentType) {
        return convertToDto(componentType);
    }

    private ComponentTypeDto convertToDto(ComponentType componentType) {
        return new ComponentTypeDto(
                componentType.getName(),
                componentType.getDescription(),
                componentType.getParameters().entrySet().stream()
                        .map(entry -> {
                            ComponentTypeParameter parameter = entry.getValue();
                            return new ComponentTypeParameterDto(
                                    entry.getKey(),
                                    parameter.getDescription(),
                                    parameter.getType(),
                                    parameter.isMandatory(),
                                    parameter.isEncrypted()
                            );
                        })
                        .collect(Collectors.toList())
        );
    }
}
