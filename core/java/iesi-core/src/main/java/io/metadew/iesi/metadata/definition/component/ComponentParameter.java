package io.metadew.iesi.metadata.definition.component;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;

public class ComponentParameter extends Metadata<ComponentParameterKey> {

    private String value;

    public ComponentParameter(ComponentParameterKey componentParameterKey, String value) {
        super(componentParameterKey);
        this.value = value;
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getComponentParameterName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}