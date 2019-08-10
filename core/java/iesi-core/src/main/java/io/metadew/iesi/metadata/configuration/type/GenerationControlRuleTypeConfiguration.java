package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.GenerationControlRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationControlRuleTypeConfiguration {

    private GenerationControlRuleType generationControlRuleType;
    private String dataObjectType = "GenerationControlRuleType";

    // Constructors
    public GenerationControlRuleTypeConfiguration(GenerationControlRuleType generationControlRuleType) {
        this.setgenerationControlRuleType(generationControlRuleType);
    }

    public GenerationControlRuleTypeConfiguration() {
    }

    //	// Methods
    public GenerationControlRuleType getGenerationControlRuleType(String GenerationControlRuleTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), GenerationControlRuleTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
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

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}