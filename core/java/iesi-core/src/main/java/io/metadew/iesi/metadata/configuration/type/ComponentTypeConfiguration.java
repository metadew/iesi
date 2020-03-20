package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;

public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private String dataObjectType = "ComponentType";

    public ComponentType getComponentType(String componentTypeName) {
        return MetadataComponentTypesConfiguration.getInstance().getComponentType(componentTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + componentTypeName + " not found"));
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

}