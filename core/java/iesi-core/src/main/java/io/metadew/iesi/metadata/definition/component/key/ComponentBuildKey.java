package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class ComponentBuildKey extends MetadataKey {
    private final String componentId;
    private final long componentVersionNb;
    private final String componentBuildName;

}
