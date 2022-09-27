package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentParameterDto extends RepresentationModel<ComponentParameterDto> {

    private String name;
    private String value;

    public ComponentParameter convertToEntity(String componentId, long versionNumber) {
        return new ComponentParameter(new ComponentParameterKey(new ComponentKey(componentId, versionNumber), name), value);
    }

}