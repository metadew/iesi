package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;

public class ComponentParameterBuilder {

    private final String componentId;
    private final long componentVersion;
    private String parameterName;
    private String value;

    public ComponentParameterBuilder(String componentId, long componentVersion, String parameterName) {
        this.componentId = componentId;
        this.componentVersion = componentVersion;
        this.parameterName = parameterName;
    }

    public ComponentParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ComponentParameter build() {
        return new ComponentParameter(new ComponentParameterKey(componentId, componentVersion, parameterName), value == null ? "dummy" : value);
    }

}
