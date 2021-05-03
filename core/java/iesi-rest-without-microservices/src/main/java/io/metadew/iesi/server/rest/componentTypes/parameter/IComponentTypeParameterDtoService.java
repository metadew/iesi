package io.metadew.iesi.server.rest.componentTypes.parameter;

import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;
import io.metadew.iesi.server.rest.componentTypes.dto.ComponentTypeParameterDto;

public interface IComponentTypeParameterDtoService {

    public ComponentTypeParameterDto convertToDto(ComponentTypeParameter componentTypeParameter, String name);
}
