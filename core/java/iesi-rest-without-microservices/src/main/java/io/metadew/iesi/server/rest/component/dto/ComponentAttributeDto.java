package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public class ComponentAttributeDto extends RepresentationModel<ComponentAttributeDto> {

    private final String environment;
    private final String name;
    private final String value;

    public ComponentAttribute convertToEntity(String componentId, long versionNumber) {
        return new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(componentId, versionNumber), new EnvironmentKey(environment), name), value);
    }
}