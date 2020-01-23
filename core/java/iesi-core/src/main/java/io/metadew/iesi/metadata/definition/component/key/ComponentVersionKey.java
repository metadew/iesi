package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentVersionKey extends MetadataKey {
    private final ComponentKey componentKey;

    public ComponentVersionKey(String componentId, long componentVersionNumber) {
        this.componentKey = new ComponentKey(componentId, componentVersionNumber);
    }

    public ComponentVersionKey(ComponentKey componentKey) {
        this.componentKey = componentKey;
    }

    public ComponentKey getComponentKey() {
        return componentKey;
    }
}
