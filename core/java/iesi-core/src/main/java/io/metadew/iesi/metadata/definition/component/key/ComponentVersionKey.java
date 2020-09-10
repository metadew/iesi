package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ComponentVersionKey extends MetadataKey {

    private final ComponentKey componentKey;

    public ComponentVersionKey(String componentId, long componentVersionNumber) {
        this.componentKey = new ComponentKey(componentId, componentVersionNumber);
    }

}
