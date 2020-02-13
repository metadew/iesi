package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ComponentAttributeKey extends MetadataKey {
    private final ComponentKey componentKey;
    private final EnvironmentKey environmentKey;
    private final String componentAttributeName;

    public ComponentAttributeKey(String componentId, long componentVersionNb, String environment, String componentAttributeName) {
        this.componentKey = new ComponentKey(componentId, componentVersionNb);
        this.environmentKey = new EnvironmentKey(environment);
        this.componentAttributeName = componentAttributeName;
    }

}
