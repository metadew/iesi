package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.SubroutineType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class SubroutineTypeConfiguration {

    private SubroutineType subroutineType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "SubroutineType";

    // Constructors
    public SubroutineTypeConfiguration(SubroutineType subroutineType, FrameworkExecution frameworkExecution) {
        this.setSubroutineType(subroutineType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public SubroutineTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public SubroutineType getSubroutineType(String subroutineTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
                this.getDataObjectType(), subroutineTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
        ObjectMapper objectMapper = new ObjectMapper();
        SubroutineType subroutineType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                SubroutineType.class);
        return subroutineType;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public SubroutineType getSubroutineType() {
        return subroutineType;
    }

    public void setSubroutineType(SubroutineType subroutineType) {
        this.subroutineType = subroutineType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }
}