package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;

public class ComponentParameterBuilder {

    private final String componentName;
    private final long componentVersion;
    private String parameterName;
    private String value;

    public ComponentParameterBuilder(String componentName, long componentVersion, String parameterName) {
        this.componentName = componentName;
        this.componentVersion = componentVersion;
        this.parameterName = parameterName;
    }

    public ComponentParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ComponentParameter build() {
        return new ComponentParameter(new ComponentParameterKey(componentName, componentVersion, parameterName), value == null ? "dummy" : value);
    }

}
