package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;

public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private String dataObjectType = "ComponentType";

    // Constructors
    public ComponentTypeConfiguration(ComponentType componentType) {
        this.setComponentType(componentType);
    }

    public ComponentTypeConfiguration() {
    }

    public ComponentType getComponentType(String componentTypeName) {
        return MetadataComponentTypesConfiguration.getInstance().getComponentType(componentTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + componentTypeName + " not found"));
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

}