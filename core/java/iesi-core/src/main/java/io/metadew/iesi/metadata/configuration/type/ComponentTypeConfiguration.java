package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

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
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), componentTypeName);
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

}