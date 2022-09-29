package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private String dataObjectType = "ComponentType";

    private final MetadataComponentTypesConfiguration metadataComponentTypesConfiguration;

    public ComponentTypeConfiguration(MetadataComponentTypesConfiguration metadataComponentTypesConfiguration) {
        this.metadataComponentTypesConfiguration = metadataComponentTypesConfiguration;
    }

    public Optional<ComponentType> getComponentType(String componentTypeName) {
        return metadataComponentTypesConfiguration.getComponentType(componentTypeName);
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

}