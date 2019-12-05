package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentAttributeKey extends MetadataKey {
    private String componentId;
    private long componentVersionNb;
    private String componentAttributeName;

    public ComponentAttributeKey(String componentId, long componentVersionNb, String componentAttributeName) {
        this.componentId = componentId;
        this.componentVersionNb = componentVersionNb;
        this.componentAttributeName = componentAttributeName;
    }

    public String getComponentId() {
        return componentId;
    }

    public long getComponentVersionNb() {
        return componentVersionNb;
    }

    public String getComponentAttributeName() {
        return componentAttributeName;
    }
}
