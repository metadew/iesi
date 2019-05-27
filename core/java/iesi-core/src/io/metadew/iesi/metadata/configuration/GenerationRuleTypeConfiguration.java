package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationRuleTypeConfiguration {

    private GenerationRuleType generationRuleType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "GenerationRuleType";


    // Constructors
    public GenerationRuleTypeConfiguration(GenerationRuleType generationRuleType, FrameworkExecution frameworkExecution) {
        this.setgenerationRuleType(generationRuleType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public GenerationRuleTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Methods
    public GenerationRuleType getGenerationRuleType(String GenerationRuleTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
                this.getDataObjectType(), GenerationRuleTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerationRuleType GenerationRuleType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                GenerationRuleType.class);
        return GenerationRuleType;
    }


    // Getters and Setters
    public GenerationRuleType getgenerationRuleType() {
        return generationRuleType;
    }

    public void setgenerationRuleType(GenerationRuleType generationRuleType) {
        this.generationRuleType = generationRuleType;
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