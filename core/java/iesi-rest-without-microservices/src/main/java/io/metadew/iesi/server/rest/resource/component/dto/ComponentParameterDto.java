package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ComponentParameterDto extends Dto {

    private final String name;
    private final String value;

    public ComponentParameter convertToEntity(String componentId, long versionNumber) {
        return new ComponentParameter(new ComponentParameterKey(new ComponentKey(componentId, versionNumber), name), value);
    }

}