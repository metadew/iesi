package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ComponentParameterKey extends MetadataKey {
    private final ComponentKey componentKey;
    private String parameterName;

    public ComponentParameterKey(String componentId, long componentVersionNb, String parameterName) {
        this.componentKey = new ComponentKey(componentId, componentVersionNb);
        this.parameterName = parameterName;
    }

    public ComponentParameterKey(ComponentKey componentKey, String parameterName) {
        this.componentKey = componentKey;
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ComponentKey getComponentKey() {
        return componentKey;
    }
}
