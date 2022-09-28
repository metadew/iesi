package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;
import org.springframework.stereotype.Component;

@Component
public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private String dataObjectType = "ComponentType";

    private final MetadataComponentTypesConfiguration metadataComponentTypesConfiguration;

    public ComponentTypeConfiguration(MetadataComponentTypesConfiguration metadataComponentTypesConfiguration) {
        this.metadataComponentTypesConfiguration = metadataComponentTypesConfiguration;
    }

    public ComponentType getComponentType(String componentTypeName) {
        return metadataComponentTypesConfiguration.getComponentType(componentTypeName)
                .orElseThrow(() -> new RuntimeException("connection type " + componentTypeName + " not found"));
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

}