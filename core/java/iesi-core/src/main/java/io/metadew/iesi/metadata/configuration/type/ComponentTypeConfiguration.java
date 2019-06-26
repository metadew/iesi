package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "ComponentType";

    // Constructors
    public ComponentTypeConfiguration(ComponentType componentType, FrameworkInstance frameworkInstance) {
        this.setComponentType(componentType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentType getComponentType(String componentTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(), this.getDataObjectType(), componentTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ComponentType componentType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ComponentType.class);
        return componentType;
    }

    // Getters and Setters
    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}