package io.metadew.iesi.server.rest.componentTypes.dto;

import io.metadew.iesi.metadata.definition.component.ComponentTypeParameter;

public interface IComponentTypeParameterService {
    public ComponentTypeParameterDto convertToDto(ComponentTypeParameter componentTypeParameter, String name );
}
