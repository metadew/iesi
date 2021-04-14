package io.metadew.iesi.server.rest.componentTypes;

import io.metadew.iesi.metadata.definition.component.ComponentType;

public interface IComponentTypeDtoService {
    public ComponentTypeDto convertToDto(ComponentType actionType, String name);
}
