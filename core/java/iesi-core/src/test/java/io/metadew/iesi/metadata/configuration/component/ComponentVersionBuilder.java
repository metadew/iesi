package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;

public class ComponentVersionBuilder {

    private final String componentId;
    private final long componentVersion;
    private String description;

    public ComponentVersionBuilder(String componentId, long componentVersion) {
        this.componentId = componentId;
        this.componentVersion = componentVersion;
    }

    public ComponentVersionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ComponentVersion build() {
        return new ComponentVersion(new ComponentVersionKey(componentId, componentVersion), description == null ? "dummy" : description);
    }

}
