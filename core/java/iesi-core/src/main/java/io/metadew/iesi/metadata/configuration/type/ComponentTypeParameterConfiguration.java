package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.definition.ComponentTypeParameter;

public class ComponentTypeParameterConfiguration {

    private ComponentTypeParameter componentTypeParameter;

    // Constructors
    public ComponentTypeParameterConfiguration(ComponentTypeParameter componentTypeParameter) {
        this.setComponentTypeParameter(componentTypeParameter);
    }

    public ComponentTypeParameterConfiguration() {
    }

    // Get Component Type Parameter
    public ComponentTypeParameter getComponentTypeParameter(String componentTypeName, String componentTypeParameterName) {
        ComponentTypeParameter componentTypeParameterResult = null;
        ComponentTypeConfiguration componentTypeConfiguration = new ComponentTypeConfiguration();
        ComponentType componentType = componentTypeConfiguration.getComponentType(componentTypeName);
        for (ComponentTypeParameter componentTypeParameter : componentType.getParameters()) {
            if (componentTypeParameter.getName().equalsIgnoreCase(componentTypeParameterName)) {
                componentTypeParameterResult = componentTypeParameter;
                break;
            }
        }
        return componentTypeParameterResult;
    }

    // Getters and Setters
    public ComponentTypeParameter getComponentTypeParameter() {
        return componentTypeParameter;
    }

    public void setComponentTypeParameter(ComponentTypeParameter componentTypeParameter) {
        this.componentTypeParameter = componentTypeParameter;
    }
}