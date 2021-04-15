package io.metadew.iesi.server.rest.componentTypes.dto;

import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;
import org.springframework.stereotype.Service;

@Service
public class ComponentTypeParameterService implements IComponentTypeParameterService{
    @Override
    public ComponentTypeParameterDto convertToDto(ComponentTypeParameter componentTypeParameter, String name) {
        return new ComponentTypeParameterDto(
                name,
                componentTypeParameter.getDescription(),
                componentTypeParameter.getType(),
                componentTypeParameter.isMandatory(),
                componentTypeParameter.isEncrypted()
        );
    }
}
