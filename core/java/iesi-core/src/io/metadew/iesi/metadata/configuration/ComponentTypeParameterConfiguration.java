package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.definition.ComponentTypeParameter;

public class ComponentTypeParameterConfiguration {

    private ComponentTypeParameter componentTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ComponentTypeParameterConfiguration(ComponentTypeParameter componentTypeParameter, FrameworkExecution processiongTools) {
        this.setComponentTypeParameter(componentTypeParameter);
        this.setFrameworkExecution(processiongTools);
    }

    public ComponentTypeParameterConfiguration(FrameworkExecution processiongTools) {
        this.setFrameworkExecution(processiongTools);
    }

    // Get Component Type Parameter
    public ComponentTypeParameter getComponentTypeParameter(String componentTypeName, String componentTypeParameterName) {
        ComponentTypeParameter componentTypeParameterResult = null;
        ComponentTypeConfiguration componentTypeConfiguration = new ComponentTypeConfiguration(this.getFrameworkExecution());
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}