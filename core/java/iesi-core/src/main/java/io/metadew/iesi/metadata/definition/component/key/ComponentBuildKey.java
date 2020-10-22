package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = false)
public class ComponentBuildKey extends MetadataKey {
    @Builder
    public ComponentBuildKey(String componentId, long componentVersionNb, String componentBuildName) {
        this.componentId = componentId;
        this.componentVersionNb = componentVersionNb;
        this.componentBuildName = componentBuildName;
    }

    private final String componentId;
    private final long componentVersionNb;
    private final String componentBuildName;

}
