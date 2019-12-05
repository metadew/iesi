package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentBuildKey extends MetadataKey {
    private String componentId;
    private long componentVersionNb;
    private String componentBuildName;

    public ComponentBuildKey(String componentId, long componentVersionNb, String componentBuildName) {
        this.componentId = componentId;
        this.componentVersionNb = componentVersionNb;
        this.componentBuildName = componentBuildName;
    }

    public String getComponentId() {
        return componentId;
    }

    public long getComponentVersionNb() {
        return componentVersionNb;
    }

    public String getComponentBuildName() {
        return componentBuildName;
    }
}
