package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentVersionKey extends MetadataKey {
    private String componentId;
    private long componentVersionNumber;

    public ComponentVersionKey(String componentId, long componentVersionNumber) {
        this.componentId = componentId;
        this.componentVersionNumber = componentVersionNumber;
    }

    public String getComponentId() {
        return componentId;
    }

    public long getComponentVersionNumber() {
        return componentVersionNumber;
    }
}
