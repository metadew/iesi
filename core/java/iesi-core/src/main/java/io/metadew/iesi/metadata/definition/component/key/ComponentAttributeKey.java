package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentAttributeKey extends MetadataKey {
    private final ComponentKey componentKey;
    private final EnvironmentKey environmentKey;
    private String componentAttributeName;

    public ComponentAttributeKey(String componentId, long componentVersionNb, String environment, String componentAttributeName) {
        this.componentKey = new ComponentKey(componentId, componentVersionNb);
        this.environmentKey = new EnvironmentKey(environment);
        this.componentAttributeName = componentAttributeName;
    }

    public ComponentAttributeKey(ComponentKey componentKey, EnvironmentKey environmentKey, String componentAttributeName) {
        this.environmentKey = environmentKey;
        this.componentKey = componentKey;
        this.componentAttributeName = componentAttributeName;
    }

    public String getComponentAttributeName() {
        return componentAttributeName;
    }

    public ComponentKey getComponentKey() {
        return componentKey;
    }

    public EnvironmentKey getEnvironmentKey() {
        return environmentKey;
    }
}
