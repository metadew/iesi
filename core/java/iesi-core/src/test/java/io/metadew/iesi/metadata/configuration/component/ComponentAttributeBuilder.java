package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;

public class ComponentAttributeBuilder {

    private final String componentId;
    private final long componentVersion;
    private final String environment;
    private final String attributeName;
    private String value;

    public ComponentAttributeBuilder(String componentId, long componentVersion, String environment, String attributeName) {
        this.componentId = componentId;
        this.componentVersion = componentVersion;
        this.environment = environment;
        this.attributeName = attributeName;
    }

    public ComponentAttributeBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ComponentAttribute build() {
        return new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(componentId, componentVersion), new EnvironmentKey(environment), attributeName), value == null ? "dummy" : value);
    }

}
