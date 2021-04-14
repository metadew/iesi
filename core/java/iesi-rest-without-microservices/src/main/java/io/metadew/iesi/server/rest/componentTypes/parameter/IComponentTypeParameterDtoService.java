package io.metadew.iesi.server.rest.componentTypes.parameter;

import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;

public interface IComponentTypeParameterDtoService {

    public ComponentTypeParameterDto convertToDto(ComponentTypeParameter componentTypeParameter, String name);
}
