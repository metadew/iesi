package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationControlRuleTypeConfiguration {

    private GenerationControlRuleType generationControlRuleType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "GenerationControlRuleType";

    // Constructors
    public GenerationControlRuleTypeConfiguration(GenerationControlRuleType generationControlRuleType, FrameworkExecution frameworkExecution) {
        this.setgenerationControlRuleType(generationControlRuleType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public GenerationControlRuleTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    //	// Methods
    public GenerationControlRuleType getGenerationControlRuleType(String GenerationControlRuleTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
                this.getDataObjectType(), GenerationControlRuleTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                GenerationControlRuleType.class);
    }

    // Getters and Setters
    public GenerationControlRuleType getgenerationControlRuleType() {
        return generationControlRuleType;
    }

    public void setgenerationControlRuleType(GenerationControlRuleType generationControlRuleType) {
        this.generationControlRuleType = generationControlRuleType;
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