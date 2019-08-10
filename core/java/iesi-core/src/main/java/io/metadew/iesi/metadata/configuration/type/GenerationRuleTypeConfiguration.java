package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class GenerationRuleTypeConfiguration {

    private GenerationRuleType generationRuleType;
    private String dataObjectType = "GenerationRuleType";


    // Constructors
    public GenerationRuleTypeConfiguration(GenerationRuleType generationRuleType) {
        this.setgenerationRuleType(generationRuleType);
    }

    public GenerationRuleTypeConfiguration() {
    }

    // Methods
    public GenerationRuleType getGenerationRuleType(String GenerationRuleTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), GenerationRuleTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
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

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}