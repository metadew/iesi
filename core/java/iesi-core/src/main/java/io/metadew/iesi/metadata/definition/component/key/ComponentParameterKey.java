package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentParameterKey extends MetadataKey {
    private String componentId;
    private long componentVersionNb;
    private String componentParameterName;

    public ComponentParameterKey(String componentId, long componentVersionNb, String componentParameterName) {
        this.componentId = componentId;
        this.componentVersionNb = componentVersionNb;
        this.componentParameterName = componentParameterName;
    }

    public String getComponentId() {
        return componentId;
    }

    public long getComponentVersionNb() {
        return componentVersionNb;
    }

    public String getComponentParameterName() {
        return componentParameterName;
    }
}
