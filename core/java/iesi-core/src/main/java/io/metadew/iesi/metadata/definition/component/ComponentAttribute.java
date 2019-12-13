package io.metadew.iesi.metadata.definition.component;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;

public class ComponentAttribute extends Metadata<ComponentAttributeKey> {

    private String environment;
    private String value;


    public ComponentAttribute(ComponentAttributeKey componentAttributeKey, String environment, String value) {
        super(componentAttributeKey);
        this.environment = environment;
        this.value = value;
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getComponentAttributeName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}