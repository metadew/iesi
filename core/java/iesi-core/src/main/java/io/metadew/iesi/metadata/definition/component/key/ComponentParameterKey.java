package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentParameterKey extends MetadataKey {
    private final ComponentKey componentKey;
    private String componentParameterName;

    public ComponentParameterKey(String componentId, long componentVersionNb, String componentParameterName) {
        this.componentKey = new ComponentKey(componentId, componentVersionNb);
        this.componentParameterName = componentParameterName;
    }

    public ComponentParameterKey(ComponentKey componentKey, String componentParameterName) {
        this.componentKey = componentKey;
        this.componentParameterName = componentParameterName;
    }

    public String getComponentParameterName() {
        return componentParameterName;
    }

    public ComponentKey getComponentKey() {
        return componentKey;
    }
}
