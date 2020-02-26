package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ComponentParameterKey extends MetadataKey {

    private final ComponentKey componentKey;
    private final String parameterName;

    public ComponentParameterKey(String componentId, long componentVersionNb, String parameterName) {
        this.componentKey = new ComponentKey(componentId, componentVersionNb);
        this.parameterName = parameterName;
    }
}
