package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.definition.ComponentTypeParameter;

public class ComponentTypeParameterConfiguration {

    private ComponentTypeParameter componentTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ComponentTypeParameterConfiguration(ComponentTypeParameter componentTypeParameter, FrameworkInstance frameworkInstance) {
        this.setComponentTypeParameter(componentTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get Component Type Parameter
    public ComponentTypeParameter getComponentTypeParameter(String componentTypeName, String componentTypeParameterName) {
        ComponentTypeParameter componentTypeParameterResult = null;
        ComponentTypeConfiguration componentTypeConfiguration = new ComponentTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}