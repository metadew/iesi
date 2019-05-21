package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ComponentTypeConfiguration {

    private ComponentType componentType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "ComponentType";

    // Constructors
    public ComponentTypeConfiguration(ComponentType componentType, FrameworkExecution frameworkExecution) {
        this.setComponentType(componentType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ComponentTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public ComponentType getComponentType(String componentTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(), this.getDataObjectType(), componentTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}