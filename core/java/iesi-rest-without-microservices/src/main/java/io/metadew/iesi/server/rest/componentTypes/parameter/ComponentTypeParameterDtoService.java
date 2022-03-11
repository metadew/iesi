package io.metadew.iesi.server.rest.componentTypes.parameter;

import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeParameterDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnWebApplication
public class ComponentTypeParameterDtoService implements IComponentTypeParameterDtoService{

    public ComponentTypeParameterDto convertToDto(ComponentTypeParameter componentTypeParameter, String name) {
        return new ComponentTypeParameterDto(name, componentTypeParameter.getDescription(), componentTypeParameter.getType(),
                componentTypeParameter.isMandatory(), componentTypeParameter.isEncrypted());
    }
}
